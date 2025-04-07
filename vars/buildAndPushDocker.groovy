 def call(String imageName, String tag, String repoUrl, String branch ) {
    pipeline {
        agent any
        environment {
         DOCKER_CREDS = credentials('mohamedesmael_dockerhub')
  //     imageName = 'mohamedesmael/jenkins_lab1'
  //      tag = '100'
                    }
        stages {
            stage('Checkout') {
                steps {
                    git url: 'https://github.com/mohamedesmael10/Jenkins_lab2.git', branch: 'main''
                }
            }

          stage('Docker Login') {
            steps {
                sh '''
                  echo "\$DOCKER_CREDS_PSW" | docker login -u "\$DOCKER_CREDS_USR" --password-stdin
                '''
                  }
            }  
          
            stage('Build Image') {
                steps {
                    echo "Building Docker image ${imageName}:${tag}"
                    sh "docker build -t ${imageName}:${tag} ."
                }
            }
            stage('Tag Image') {
                steps {
                    echo "Tagging image as ${imageName}:latest"
                    sh "docker tag ${imageName}:${tag} ${imageName}:latest"
                }
            }
            stage('Push Image') {
                steps {
                    echo "Pushing image ${imageName}:${tag} and ${imageName}:latest"
                    sh "docker push ${imageName}:${tag}"
                    sh "docker push ${imageName}:latest"
                }
            }
        }
    }
}
