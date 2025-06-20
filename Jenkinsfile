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

//         stage('Test') {
//             steps {
//                 sh './gradlew test'
//             }
//         }

        // Docker 배포 시 넣는거로
//         stage('Docker Build') {
//             steps {
//                 sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
//             }
//         }

        stage('Deploy') {
            steps {
                sshagent(['iryeokfit-server-key']) {
                    sh '''
                        scp build/libs/*.jar junhyung@14.42.39.186:~/deploy/
                        ssh junhyung@14.42.39.186 'bash ~/deploy/restart.sh'
                    '''
                }
            }
        }
    }
}
