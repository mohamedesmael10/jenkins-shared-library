// vars/buildAndPushDocker.groovy

def call(String imageName, String tag, String repoUrl, String branch) {
    // wrap the entire build in a node block targeting label 'ub'
    node('ub') {
        stage('Checkout') {
            checkout([$class: 'GitSCM',
                      branches: [[name: branch]],
                      userRemoteConfigs: [[
                          url: repoUrl,
                          credentialsId: 'mohamedesmael10'
                      ]]
            ])
        }

        stage('Install Docker') {
            // requires that the Jenkins user can sudo without a password,
            // or that the agent is already root
            sh '''
                if ! command -v docker >/dev/null 2>&1; then
                  echo "Docker not found – installing…"
                  curl -fsSL https://get.docker.com | sudo sh
                  # ensure jenkins user is in docker group
                  sudo usermod -aG docker $USER || true
                  # re‑evaluate group membership in this shell
                  newgrp docker || true
                else
                  echo "Docker already installed"
                fi
            '''
        }

        stage('Docker Login') {
            withCredentials([usernamePassword(
                credentialsId: 'mohamedesmael_dockerhub',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {
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
}
