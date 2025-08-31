pipeline {
    agent any

    tools {
        maven 'maven:3.9.7'
    }

    environment {
        DOCKER_IMAGE = "vvsslova/asset-spy-rfid-emu-service"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        GITHUB_REPO = "vvsslova/asset-spy-rfid-emu-service"

        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig'
    }

    stages {
        stage('Checkout') {
             steps {
                 script {
                        setGitHubCommitStatus('PENDING', 'Pipeline started', 'Checkout code')
                 }
                 checkout scm
             }
        }

        stage('Test') {
             steps {
                  script {
                        setGitHubCommitStatus('PENDING','Running tests','Test stage in progress')
                  }
                  sh 'mvn -B test'
             }
        }

        stage('Build with Maven') {
        	steps {
        		script {
        			setGitHubCommitStatus('PENDING','Building project','Build stage in progress')
        		}
        		sh 'mvn -B -DskipTests=true clean package'
        	}
        }

        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                script {
                    setGitHubCommitStatus('PENDING','Building Docker image','Docker build in progress')
                }
                 sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} . '
            }
        }

        stage('Push to DockerHub') {
            when {
                branch 'main'
            }
            steps {
                script {
                    setGitHubCommitStatus('PENDING', 'Pushing Docker image to Docker Hub', 'Docker push in progress')
                }
                withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh """
                    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                    """
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when {
                branch 'main'
            }
            steps {
            	script {
            		setGitHubCommitStatus('PENDING','Deploying to Kubernetes','Kubernetes deployment in progress')
            	}
            	withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG')]) {
            	sh '''
            	kubectl set image deploy/rfid-emu-service rfid-emu-service=${DOCKER_IMAGE}:${DOCKER_TAG} -n asset-spy
            	kubectl rollout status deploy/rfid-emu-service --timeout=180s -n asset-spy
            	'''
            	}
            }
        }
    }

    post {
        success {
           echo "Deploy image with tag: ${DOCKER_TAG} was successful"
        }
        failure {
           echo "Deploy image with tag: ${DOCKER_TAG} failed"
        }
        always {
             script {
                   def contexts = [
                        'Checkout code',
                        'Build stage in progress',
                        'Docker build in progress',
                        'Docker push in progress',
                        'Kubernetes deployment in progress'
                   ]
                   contexts.each { context ->
                        setGitHubCommitStatus(currentBuild.result ?: 'SUCCESS', 'Pipeline finished', context)
                   }
             }
        }
    }
}

def setGitHubCommitStatus(state, description, context) {
     step([
            $class: 'GitHubCommitStatusSetter',
            reposSource: [$class: 'ManuallyEnteredRepositorySource', url: "https://github.com/${env.GITHUB_REPO}"],
            contextSource: [$class: 'ManuallyEnteredCommitContextSource', context: context],
            statusResultSource: [
                $class: 'ConditionalStatusResultSource',
                results: [
                    [$class: 'AnyBuildResult', state: state, message: description]
                ]
            ]
     ])
}