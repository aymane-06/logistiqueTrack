#!/bin/bash

# 🚀 Script de démarrage de l'infrastructure d'observabilité LogiTrack

echo "========================================="
echo "🚀 LogiTrack Observability Stack Setup"
echo "========================================="
echo ""

# Vérifier que Docker est en cours d'exécution
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker n'est pas en cours d'exécution. Veuillez le démarrer."
    exit 1
fi

echo "✅ Docker est actif"
echo ""

# Créer le dossier logs s'il n'existe pas
if [ ! -d "logs" ]; then
    echo "📁 Création du dossier logs..."
    mkdir -p logs
    echo "✅ Dossier logs créé"
else
    echo "✅ Dossier logs existe déjà"
fi

echo ""
echo "🐳 Démarrage des services Docker..."
echo "   - PostgreSQL"
echo "   - Elasticsearch"
echo "   - Kibana"
echo "   - Filebeat"
echo ""

# Démarrer les services
docker-compose up -d postgres elasticsearch kibana filebeat

echo ""
echo "⏳ Attente du démarrage d'Elasticsearch..."
sleep 10

# Vérifier qu'Elasticsearch est prêt
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if curl -s http://localhost:9200/_cluster/health > /dev/null 2>&1; then
        echo "✅ Elasticsearch est prêt!"
        break
    fi
    echo "   Tentative $((attempt+1))/$max_attempts..."
    sleep 2
    attempt=$((attempt+1))
done

if [ $attempt -eq $max_attempts ]; then
    echo "❌ Elasticsearch n'a pas démarré dans les temps"
    echo "   Vérifiez les logs: docker-compose logs elasticsearch"
    exit 1
fi

echo ""
echo "⏳ Attente du démarrage de Kibana..."
sleep 15

# Vérifier que Kibana est prêt
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if curl -s http://localhost:5601/api/status > /dev/null 2>&1; then
        echo "✅ Kibana est prêt!"
        break
    fi
    echo "   Tentative $((attempt+1))/$max_attempts..."
    sleep 2
    attempt=$((attempt+1))
done

if [ $attempt -eq $max_attempts ]; then
    echo "❌ Kibana n'a pas démarré dans les temps"
    echo "   Vérifiez les logs: docker-compose logs kibana"
    exit 1
fi

echo ""
echo "========================================="
echo "✅ Infrastructure démarrée avec succès!"
echo "========================================="
echo ""
echo "📊 Accès aux services:"
echo "   - Kibana:        http://localhost:5601"
echo "   - Elasticsearch: http://localhost:9200"
echo "   - PostgreSQL:    localhost:5432"
echo ""
echo "📝 Prochaines étapes:"
echo "   1. Démarrer l'application Spring Boot: ./mvnw spring-boot:run"
echo "   2. Ouvrir Kibana: http://localhost:5601"
echo "   3. Consulter le guide: OBSERVABILITY_GUIDE.md"
echo ""
echo "🔍 Commandes utiles:"
echo "   - Voir les logs: docker-compose logs -f [service]"
echo "   - Arrêter: docker-compose down"
echo "   - Statut: docker-compose ps"
echo ""
echo "========================================="
