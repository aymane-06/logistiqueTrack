// Fichier: Jenkinsfile
pipeline {
    // 1. Définition de l'agent
    // Nous utilisons un agent Docker pour garantir que nous avons 
    // Maven 3.9 et Java 17 (Temurin), comme défini dans votre pom.xml.
    agent {
        docker { 
            image 'maven:3.9.6-eclipse-temurin-17' 
            // On partage le cache .m2 local pour accélérer les builds futurs
            args '-v $HOME/.m2:/root/.m2' 
        }
    }

    stages {
        // 2. Étape de Build et Test
        // Compile, exécute les tests unitaires et génère le rapport JaCoCo
        stage('Build & Test') {
            steps {
                // Utilise le Maven Wrapper (mvnw) si présent, sinon 'mvn'
                sh "mvn -B clean verify"
            }
        }

        // 3. Étape d'analyse SonarQube
        stage('SonarQube Analysis & Quality Gate') {
            steps {
                // 'SonarQube' est le nom que nous avons défini dans "Configure System"
                withSonarQubeEnv('SonarQube') {
                    // Jenkins injecte le token (SONAR_TOKEN) et l'URL du serveur
                    // Le 'pom.xml' lit automatiquement sonar.host.url
                    // Nous lançons la cible 'sonar:sonar'
                    sh "mvn sonar:sonar"
                }

                // Vérifie le Quality Gate défini dans votre cahier des charges
                // Le pipeline attendra que SonarQube termine son analyse
                timeout(time: 1, unit: 'MINUTES') {
                    // 'waitForQualityGate' vient du plugin SonarQube Scanner
                    // 'abortPipeline: true' fera échouer le build si le QG échoue
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    // 4. Actions post-build (toujours exécutées)
    post {
        always {
            // Publie les résultats des tests unitaires
            junit 'target/surefire-reports/**/*.xml'
            
            // Publie le rapport de couverture de code JaCoCo
            jacoco(
                execPattern: 'target/jacoco.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java'
            )
            
            // Archive l'artefact (le .war)
            archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
        }
    }
}