pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE = 'tingdulal/calcops'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                        -t ${DOCKER_IMAGE}:${BUILD_NUMBER} \
                        -t ${DOCKER_IMAGE}:latest \
                        .
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "$DOCKERHUB_TOKEN" | docker login \
                            --username "$DOCKERHUB_USER" \
                            --password-stdin

                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -e

                    echo "Deploying CalcOps image: ${DOCKER_IMAGE}:${BUILD_NUMBER}"

                    IMAGE_TAG="${BUILD_NUMBER}" docker compose -p calcops pull calcops

                    IMAGE_TAG="${BUILD_NUMBER}" docker compose -p calcops up \
                        -d --no-build --force-recreate calcops nginx

                    echo "Waiting for CalcOps health check..."

                    for i in $(seq 1 30); do
                        if curl -fsS http://localhost:8081/actuator/health \
                            | grep -q '"status":"UP"'; then
                            echo "CalcOps is healthy!"
                            exit 0
                        fi
                        sleep 2
                    done

                    echo "Deployment health check failed!"
                    docker compose -p calcops ps
                    exit 1
                '''
            }
        }
    }

    post {
    always {
        emailext(
            subject: "CalcOps Jenkins Build #${BUILD_NUMBER} - ${currentBuild.currentResult}",
            body: """\
                Project: CalcOps
                Build: #${BUILD_NUMBER}
                Result: ${currentBuild.currentResult}
                Build URL: ${BUILD_URL}
                """,
                    to: 'siddniya@gmail.com,siddharthanil084@gmail.com'
            )
        }
    }
}