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
                    sh "mvn sonar:sonar -Dsonar.qualitygate.wait=true -Dsonar.qualitygate.timeout=300"
                }
                
                echo "SonarQube analysis completed successfully"
            }
        }
        // ... après le stage SonarQube ...

       stage('Build & Push Docker Image') {
            steps {
                script {
                    // 1. Define the image name (Username + Project Name + Tag)
                    // We use the Jenkins BUILD_NUMBER as a tag for versioning (e.g., v1, v2...)
                    def imageName = "aymanehimame/logitrack:${env.BUILD_NUMBER}"
                    
                    // 2. Build the image locally
                    // This uses the Dockerfile at the root of your project
                    def customImage = docker.build(imageName)
                    
                    // 3. Login and Push to Registry
                    // The '' represents the default registry (Docker Hub)
                    // 'docker-hub-credentials' matches the ID you created in Step 2
                    docker.withRegistry('', 'docker-hub-credentials') {
                        customImage.push()
                        
                        // Optional: Also push as 'latest' so it's easy to pull the newest one
                        customImage.push('latest')
                    }
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