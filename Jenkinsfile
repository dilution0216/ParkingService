pipeline {
    agent any

    environment {
        BITBUCKET_CREDENTIALS = credentials('hskim1')
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'hskim1',
                    url: 'https://hskim1@bitbucket.org/dhicc/sp1-week2.git',
                    branch: 'feature/hskim1'
            }
        }
        stage('Build') {
            steps {
                sh "mvn clean package -X"
            }
        }
        stage('Test') {
            steps {
                sh "mvn test"
            }
        }
        stage('Deploy') {
            steps {
                echo "🚀 배포 단계 실행 (실제 배포 스크립트 추가 필요)"
            }
        }
    }
}
