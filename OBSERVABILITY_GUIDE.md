# 📊 Observabilité avec Elasticsearch & Kibana - Guide Complet

## 🎯 Vue d'ensemble

Ce système de logging centralise tous les logs applicatifs, de sécurité et métier dans Elasticsearch, avec visualisation via Kibana. Il assure la traçabilité complète des opérations et facilite le diagnostic des incidents.

---

## 🚀 Démarrage Rapide

### 1. Démarrer l'infrastructure

```bash
# Démarrer tous les services (Postgres, Elasticsearch, Kibana, Filebeat)
docker-compose up -d

# Vérifier que tous les services sont démarrés
docker-compose ps

# Vérifier les logs
docker-compose logs -f elasticsearch
docker-compose logs -f kibana
docker-compose logs -f filebeat
```

### 2. Accéder aux interfaces

- **Kibana**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200
- **Application**: http://localhost:8080

### 3. Vérifier la santé d'Elasticsearch

```bash
curl http://localhost:9200/_cluster/health?pretty
```

Réponse attendue: `"status": "green"` ou `"yellow"`

---

## 📋 Architecture des Logs

### Types de logs générés

1. **Logs Applicatifs** (`application.json`)
   - Tous les logs de l'application Spring Boot
   - Requêtes HTTP, erreurs, warnings
   - Index Elasticsearch: `logitrack-application-YYYY.MM.DD`

2. **Logs de Sécurité** (`security-audit.json`)
   - Authentifications (succès/échec)
   - Refresh de tokens
   - Tentatives d'accès non autorisées (401/403)
   - Enregistrement/déconnexion d'utilisateurs
   - Index Elasticsearch: `logitrack-security-YYYY.MM.DD`

3. **Logs Métier** (`business-audit.json`)
   - Création/modification de commandes
   - Mouvements de stock
   - Expéditions
   - Alertes de stock
   - Index Elasticsearch: `logitrack-business-YYYY.MM.DD`

### Structure des logs JSON

Tous les logs contiennent au minimum:
```json
{
  "@timestamp": "2025-12-26T15:30:45.123Z",
  "level": "INFO",
  "logger_name": "com.logitrack.logitrack.audit.SecurityAuditService",
  "message": "User logged in successfully",
  "application": "logitrack",
  "environment": "dev",
  "event_type": "authentication_success",
  "user_email": "client@example.com",
  "user_id": "uuid",
  "user_role": "ROLE_CLIENT"
}
```

**⚠️ Sécurité**: Aucun mot de passe, token ou secret n'est jamais loggé!

---

## 🔍 Utilisation de Kibana

### Premier accès

1. Ouvrir http://localhost:5601
2. Attendre le chargement complet de Kibana (~1-2 minutes au premier démarrage)
3. Cliquer sur le menu hamburger (☰) en haut à gauche

### Configuration initiale - Data Views

**⚠️ Important**: Vous devez d'abord générer des logs avant de créer les data views correspondants. Elasticsearch ne peut pas créer un data view pour un index qui n'existe pas encore.

**Ordre recommandé**:
1. ✅ Créer `logitrack-application-*` immédiatement (toujours disponible)
2. ✅ Créer `logitrack-security-*` après avoir fait au moins une tentative de connexion
3. ⏳ Créer `logitrack-business-*` après avoir créé une commande ou un mouvement de stock

---

1. **Menu** → **Stack Management** → **Data Views**
2. Cliquer sur **Create data view**

#### Data View 1: Logs Applicatifs
- **Name**: `LogiTrack Application Logs`
- **Index pattern**: `logitrack-application-*`
- **Timestamp field**: `@timestamp`
- **Save**

#### Data View 2: Logs de Sécurité
- **Name**: `LogiTrack Security Audit`
- **Index pattern**: `logitrack-security-*`
- **Timestamp field**: `@timestamp`
- **Save**

#### Data View 3: Logs Métier
- **Name**: `LogiTrack Business Audit`
- **Index pattern**: `logitrack-business-*`
- **Timestamp field**: `@timestamp`
- **Save**

**⚠️ Note**: Ce data view ne fonctionnera qu'après avoir généré des événements métier (création de commande, mouvement de stock, etc.). Voir la section "Générer des logs de test" ci-dessous.

---

## 🧪 Générer des Logs de Test

### Pour créer des logs de sécurité

```bash
# Login réussi (nécessite un utilisateur existant)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@warehouse.com","password":"votre_mot_de_passe"}'

# Login échoué
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrong"}'
```

### Pour créer des logs métier

```bash
# 1. D'abord, se connecter pour obtenir un token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@warehouse.com","password":"password"}' \
  | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

# 2. Créer une commande (exemple - adapter selon votre API)
curl -X POST http://localhost:8080/api/sales-orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "uuid-du-client",
    "warehouseId": "uuid-du-warehouse",
    "orderLines": [
      {
        "productId": "uuid-du-produit",
        "quantity": 5,
        "unitPrice": 100.00
      }
    ]
  }'
```

**Astuce**: Utilisez Postman ou l'interface Swagger (http://localhost:8080/swagger-ui.html) pour créer facilement des commandes et générer des logs métier.

---

## 🔎 Recherche de Logs

### Accéder à Discover

**Menu** → **Analytics** → **Discover**

### Exemples de recherches

#### 1. Recherche par événement de sécurité

```
event_type: "authentication_failure"
```

#### 2. Recherche par utilisateur

```
user_email: "client@example.com"
```

#### 3. Recherche par commande (ID métier)

```
order_id: "123e4567-e89b-12d3-a456-426614174000"
```

#### 4. Erreurs d'authentification (401)

```
status_code: 401
```

#### 5. Accès refusés (403)

```
status_code: 403 AND event_type: "authorization_failure"
```

#### 6. Mouvements de stock pour un produit

```
event_type: "inventory_movement" AND product_id: "product-uuid"
```

#### 7. Cycle de vie complet d'une commande

```
order_id: "order-uuid" AND business_entity: "sales_order"
```

#### 8. Alertes de stock bas

```
event_type: "stock_alert" AND alert_type: "LOW_STOCK"
```

#### 9. Toutes les actions d'un utilisateur

```
user_email: "admin@example.com" AND @timestamp >= "2025-12-26T00:00:00"
```

#### 10. Erreurs applicatives

```
level: "ERROR"
```

### Filtres avancés

Dans Kibana Discover:
1. Cliquer sur **Add filter**
2. Sélectionner le champ (ex: `event_type`)
3. Choisir l'opérateur (`is`, `is not`, `exists`, etc.)
4. Entrer la valeur

---

## 📊 Création de Visualisations

### Dashboard 1: Sécurité

#### Graphique 1: Tentatives de connexion (succès vs échec)

1. **Menu** → **Analytics** → **Dashboard** → **Create dashboard**
2. **Create visualization**
3. Type: **Vertical bar chart**
4. Data view: `LogiTrack Security Audit`
5. Horizontal axis: `@timestamp` (Date histogram, interval: Auto)
6. Breakdown: `event_type.keyword`
7. Filter: `event_type: (authentication_success OR authentication_failure)`
8. **Save**: "Login Attempts"

#### Graphique 2: Erreurs 401/403 par endpoint

1. **Create visualization**
2. Type: **Data table**
3. Data view: `LogiTrack Security Audit`
4. Rows: `endpoint.keyword`
5. Metrics: Count
6. Filter: `status_code: (401 OR 403)`
7. **Save**: "Unauthorized Access by Endpoint"

#### Graphique 3: Top utilisateurs actifs

1. **Create visualization**
2. Type: **Pie chart**
3. Data view: `LogiTrack Security Audit`
4. Slice by: `user_email.keyword`
5. Top 10 values
6. **Save**: "Top Active Users"

### Dashboard 2: Opérations Métier

#### Graphique 1: Commandes créées par jour

1. **Create visualization**
2. Type: **Line chart**
3. Data view: `LogiTrack Business Audit`
4. Horizontal axis: `@timestamp` (Date histogram, Daily)
5. Vertical axis: Count
6. Filter: `event_type: "sales_order_created"`
7. **Save**: "Daily Orders"

#### Graphique 2: Mouvements de stock

1. **Create visualization**
2. Type: **Vertical bar chart**
3. Data view: `LogiTrack Business Audit`
4. Horizontal axis: `@timestamp`
5. Breakdown: `movement_type.keyword`
6. Filter: `event_type: "inventory_movement"`
7. **Save**: "Stock Movements"

#### Graphique 3: Alertes de stock

1. **Create visualization**
2. Type: **Metric**
3. Data view: `LogiTrack Business Audit`
4. Metric: Count
5. Filter: `event_type: "stock_alert"`
6. **Save**: "Stock Alerts Count"

#### Graphique 4: États des commandes

1. **Create visualization**
2. Type: **Donut chart**
3. Data view: `LogiTrack Business Audit`
4. Slice by: `new_status.keyword`
5. Filter: `event_type: "sales_order_status_change"`
6. **Save**: "Order Status Distribution"

---

## 🔧 Cas d'Usage Pratiques

### Cas 1: Diagnostic d'erreur de stock

**Objectif**: Un client signale que sa commande ne peut pas être réservée.

1. Aller dans **Discover**
2. Sélectionner data view: `LogiTrack Business Audit`
3. Rechercher: `order_id: "UUID-de-la-commande"`
4. Filtrer par `event_type: ("stock_reservation" OR "stock_alert" OR "inventory_movement")`
5. Analyser la chronologie des événements

### Cas 2: Traçabilité complète d'une commande

**Objectif**: Suivre toutes les étapes d'une commande du début à la fin.

1. **Discover** → `LogiTrack Business Audit`
2. Rechercher: `order_id: "UUID-de-la-commande"`
3. Trier par `@timestamp`
4. Événements attendus:
   - `sales_order_created`
   - `stock_reservation`
   - `sales_order_status_change` (CREATED → RESERVED)
   - `shipment_created`
   - `sales_order_status_change` (RESERVED → SHIPPED)
   - `shipment_status_change`
   - `sales_order_status_change` (SHIPPED → DELIVERED)

### Cas 3: Analyse des tentatives d'accès non autorisées

**Objectif**: Détecter des comportements suspects (tentatives de force brute).

1. **Discover** → `LogiTrack Security Audit`
2. Rechercher: `event_type: "authentication_failure"`
3. Grouper par `user_email.keyword`
4. Identifier les emails avec de multiples échecs

### Cas 4: Audit de sécurité pour un utilisateur

**Objectif**: Voir toutes les actions d'un utilisateur spécifique.

1. **Discover** → `LogiTrack Security Audit`
2. Rechercher: `user_email: "admin@example.com"`
3. Afficher les colonnes:
   - `@timestamp`
   - `event_type`
   - `action`
   - `endpoint`
   - `status_code`

---

## 📈 Métriques et KPIs Recommandés

### Sécurité
- Nombre de connexions réussies/échouées par jour
- Taux d'erreurs 401/403 par endpoint
- Nombre de refresh tokens par utilisateur
- Temps de réponse des endpoints sécurisés

### Métier
- Nombre de commandes créées par jour/semaine/mois
- Taux de commandes réservées/expédiées/livrées
- Nombre d'alertes de stock par entrepôt
- Volume de mouvements de stock (entrées/sorties)
- Nombre d'expéditions par transporteur

---

## 🛠️ Maintenance

### Gestion des index

Les index Elasticsearch sont créés automatiquement par jour. Pour éviter l'accumulation:

```bash
# Voir tous les index
curl http://localhost:9200/_cat/indices?v

# Supprimer les anciens index (exemple: plus de 90 jours)
curl -X DELETE "http://localhost:9200/logitrack-application-2024.09.*"
```

### Politique de rétention

Recommandations:
- **Logs applicatifs**: 30 jours
- **Logs de sécurité**: 90 jours (conformité)
- **Logs métier**: 365 jours (audit)

### Backup

```bash
# Backup Elasticsearch data volume
docker run --rm --volumes-from logitrack-elasticsearch \
  -v $(pwd):/backup ubuntu tar cvf /backup/elasticsearch-backup.tar /usr/share/elasticsearch/data
```

---

## 🐛 Dépannage

### Elasticsearch ne démarre pas

```bash
# Vérifier les logs
docker-compose logs elasticsearch

# Augmenter la mémoire si nécessaire (dans compose.yaml)
- "ES_JAVA_OPTS=-Xms1g -Xmx1g"
```

### Kibana ne se connecte pas à Elasticsearch

```bash
# Vérifier qu'Elasticsearch est démarré
curl http://localhost:9200/_cluster/health

# Redémarrer Kibana
docker-compose restart kibana
```

### Aucun log n'apparaît dans Kibana

1. Vérifier que Filebeat est démarré: `docker-compose ps filebeat`
2. Vérifier que le dossier `logs/` existe et contient des fichiers
3. Vérifier les logs Filebeat: `docker-compose logs filebeat`
4. Vérifier les index: `curl http://localhost:9200/_cat/indices?v`

### Créer manuellement les logs (test)

```bash
# Démarrer l'application Spring Boot
./mvnw spring-boot:run

# Déclencher des événements (login, création de commande, etc.)
# Les logs apparaîtront dans logs/application.json, security-audit.json, business-audit.json
```

---

## 📚 Ressources Supplémentaires

- [Documentation Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Documentation Kibana](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Logback Structured Logging](https://github.com/logfellow/logstash-logback-encoder)
- [Filebeat Reference](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)

---

## ✅ Checklist de Validation

- [ ] Elasticsearch accessible sur http://localhost:9200
- [ ] Kibana accessible sur http://localhost:5601
- [ ] Filebeat en cours d'exécution
- [ ] Dossier `logs/` créé avec fichiers JSON
- [ ] Data views créés dans Kibana
- [ ] Recherche fonctionnelle dans Discover
- [ ] Dashboard de sécurité créé
- [ ] Dashboard métier créé
- [ ] Aucun secret/mot de passe dans les logs
- [ ] Logs de sécurité contiennent user_email, event_type, status_code
- [ ] Logs métier contiennent order_id, product_id, warehouse_id

---

**🎉 Votre système d'observabilité est maintenant opérationnel!**
