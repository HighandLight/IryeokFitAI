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
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
            }
        }

        stage('Deploy') {
            steps {
                sshagent(['github-pat']) {
                    sh '''
                        scp build/libs/*.jar ubuntu@3.25.59.255:~/deploy/
                        ssh ubuntu@3.25.59.255 'bash ~/deploy/restart.sh'
                    '''
                }
            }
        }
    }
}
