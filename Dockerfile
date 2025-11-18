# Utiliser une image légère avec Java 17
FROM eclipse-temurin:17-jdk-alpine
# Créer un volume pour les fichiers temporaires
VOLUME /tmp
# Copier le JAR généré par Maven (le nom peut varier, vérifiez votre target/)
COPY target/*.war app.war
# Commande de démarrage
ENTRYPOINT ["java","-jar","/app.war"]