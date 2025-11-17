// Fichier: Jenkinsfile
pipeline {
    // 1. Agent: "any"
    // Fait tourner le build sur le contrôleur Jenkins principal.
    agent any

    // 2. Outils:
    // Demande à Jenkins d'utiliser les outils que vous venez de configurer 
    // dans "Global Tool Configuration"
    tools {
        maven 'maven-3.8.5'
        jdk 'jdk-17'
    }

    stages {
        // 3. Étape de Checkout
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // 4. Étape de Build, Test & JaCoCo
        // UNE SEULE commande Maven qui fait tout:
        // 'clean': Nettoie
        // 'verify': Compile, teste, package, ET exécute le rapport JaCoCo
        stage('Build, Test & JaCoCo') {
            steps {
                sh "mvn clean verify"
            }
        }

        // 5. Étape d'analyse SonarQube
        stage('SonarQube Analysis & Quality Gate') {
            steps {
                // 'SonarQube' est le nom du serveur que vous avez configuré
                withSonarQubeEnv('SonarQube') {
                    // 'verify' a déjà créé le rapport jacoco.exec
                    // 'sonar:sonar' va l'envoyer à SonarQube
                    sh "mvn sonar:sonar"
                }

                // Attend le résultat du Quality Gate de SonarQube
                // Si le QG échoue, le pipeline échoue ici.
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    // 6. Actions Post-Build (toujours exécutées)
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
            
            // Archive l'artefact (votre pom.xml produit un .war)
            archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
        }
        
        success {
            echo 'Pipeline exécuté avec succès!'
        }
        
        failure {
            echo 'Pipeline a échoué!'
        }
    }
}