pipeline {
    agent any

    environment {
        BITBUCKET_CREDENTIALS = credentials('bitbucket-credentials')
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'bitbucket-credentials', url: 'https://bitbucket.org/dhicc/sp1-week2.git', branch: 'main'
            }
        }
        stage('Build') {
            steps {
                bat "mvn clean package"
            }
        }
        stage('Test') {
            steps {
                bat "mvn test"
            }
        }
        stage('Deploy') {
            steps {
                echo "🚀 배포 단계 실행 (실제 배포 스크립트 추가 필요)"
            }
        }
    }
}
