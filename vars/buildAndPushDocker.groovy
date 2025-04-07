// vars/buildAndPushDocker.groovy

def call(String imageName, String tag, String repoUrl, String branch) {
    stage('Checkout') {
        checkout([$class: 'GitSCM',
                  branches: [[name: branch]],
                  userRemoteConfigs: [[url: repoUrl]]
        ])
    }

    stage('Docker Login') {
        withCredentials([usernamePassword(credentialsId: 'mohamedesmael_dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
        }
    }

    stage('Build Image') {
        sh "docker build -t ${imageName}:${tag} ."
    }

    stage('Tag Image') {
        sh "docker tag ${imageName}:${tag} ${imageName}:latest"
    }

    stage('Push Image') {
        sh "docker push ${imageName}:${tag}"
        sh "docker push ${imageName}:latest"
    }
}
