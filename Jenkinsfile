pipeline {
    agent any

    environment {
        IMAGE_NAME = "iryeokfit"
    }

    stages {
        stage('Clone') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['iryeokfit-server-key']) {
                    sh '''
                        mkdir -p ~/.ssh
                        ssh-keyscan -H 14.42.39.186 >> ~/.ssh/known_hosts
                        scp build/libs/*.jar junhyung@14.42.39.186:/home/ubuntu/iryeokfitAI/app.jar
                        ssh junhyung@14.42.39.186 'bash /home/ubuntu/iryeokfitAI/restart.sh'
                    '''
                }
            }
        }
    }
}
