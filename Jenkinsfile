// This Jenkinsfile is for eureka deploy

pipeline {
    agent {
        label 'k8s-slave'
    }
    parameters{
      choice(name: 'buildOnly',
              choices : 'NO\nYES',
              description : " This will only build the application ")
      choice(name: 'scanOnly',
              choices : 'NO\nYES',
              description : " This will only build the application ")
      choice(name: 'dockerpush',
              choices : 'NO\nYES',
              description : " This will only build the application ")
      choice(name: 'deployToDev',
              choices : 'NO\nYES',
              description : " This will only build the application ")
      choice(name: 'deployToTest',
              choices : 'NO\nYES',
              description : " This will only build the application ")
      choice(name: 'deployToStage',
              choices : 'NO\nYES',
              description : " This will only build the application ")
    }
    options { 
        // Discard old builds
        buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '5'))
    }
    environment {
        DOCKERHUB = "docker.io/vinaykumarveeraboina"
        APPLICATION_NAME = 'eureka'
        POM_VERSION = readMavenPom().getVersion()
        POM_PACKAGING = readMavenPom().getPackaging()
        DOCKER_CREDS = credentials('DockerHub')
        SONAR_URL = 'http://34.60.74.134:9000'
        SONAR_TOKEN = credentials('sonar')
    }
    tools {
        maven 'maven-3.8.8'
        jdk 'Jdk17'
    }
    stages 
    {
        stage('Build') 
        {
          when {
            anyOf{
               params.buildOnly == 'YES'
              params.dockpush  == 'YES' 
              
            }
          }
            steps {
                echo "Building the ${env.APPLICATION_NAME} application"
                sh "mvn clean package -DskipTests=true"
            }
        }
        stage('Unit-Test')
         {
          when {
            anyOf{
               params.buildOnly == 'YES'
              params.dockpush  == 'YES' 
              
            }
          }
            steps {
                echo "Testing the ${env.APPLICATION_NAME} application"
                sh "mvn test"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Sonar_Test')
         {

          when {
            anyOf{
              expression{
               params.scanOnly == 'YES' 
            }
            }
          }
            steps {
                echo " ************************* STARTING SONAR ANALYSIS with Quality gate ************************"
                withSonarQubeEnv('SonarQube') {
                    sh """
                    mvn clean verify sonar:sonar \
                     -Dsonar.projectKey=i127-eureka \
                     -Dsonar.host.url=${env.SONAR_URL} \
                     -Dsonar.login=${SONAR_TOKEN}
                    """
                }
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
          }
        /*stage('Docker-Format')
         {
            steps {
                echo "ACTUAL_FORMAT: ${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING}"
                echo "CUSTOM_FORMAT: ${APPLICATION_NAME}-${currentBuild.number}-${BRANCH_NAME}.${POM_PACKAGING}"
            }
         }*/

        stage('Docker Build and Push')
         {
          when {
            anyOf{
              expression{
               params.dockerpush == 'YES' 
            }
            }
          }
            steps {
                sh """
                ls -la
                cp ${workspace}/target/i27-${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING} ./.cicd
                ls -la ./.cicd
                echo "*********************** Building Docker Image *********************************"
                docker build --force-rm --no-cache --pull --rm=true --build-arg JAR_SOURCE=i27-${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING} -t ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT} ./.cicd
                docker images 
                echo " ***************** Docker login ************************"
                docker login -u ${DOCKER_CREDS_USR} -p ${DOCKER_CREDS_PSW}
                echo " ********************* Docker push *************************************"
                docker push ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}
                """
            }
          }
        stage('Docker deploy to DEV') 
        {
          when {
            anyOf{
              expression{
               params.deployToDev == 'YES' 
            }
            }
          }
            steps {
                script {
                    DockerDeploy('dev', '5761', '8761').call()
                }
            }
        }
        stage('Docker deploy to TEST env') 
        {
          when {
            anyOf{
              expression{
               params.deployToTest == 'YES' 
            }
            }
            steps {
                script {
                    DockerDeploy('test', '6761', '8761').call()
                }
            }
        }
        stage('Docker deploy to STAGE env')
         {
          when {
            anyOf{
              expression{
               params.deployToStage == 'YES' 
            }
            }
          }
            steps {
                script {
                    DockerDeploy('stage', '7761', '8761').call()
                }
            }
         }
    }
}

// This method is developed for deploying our app in different environments
def DockerDeploy(envdeploy, hostport, contport) {

  return{
    echo "************************ Deploying to Docker $envdeploy ********************************"

    withCredentials([usernamePassword(credentialsId: 'dockerdev', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        echo "****************** PULLING the container from docker hub ********************"
        sh """
        sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker pull ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}
        """
        script {
            try {
                echo "****************** Stopping the container ********************"
                sh """
                sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker stop ${env.APPLICATION_NAME}-$envdeploy
                """

                echo "****************** Removing the container ********************"
                sh """
                sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker rm ${env.APPLICATION_NAME}-$envdeploy
                """
            } catch (err) {
                echo "Caught the Error: ${err}"
            }
        }

        echo "**************** Running the container *****************"
        sh """
        sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker run -d -p $hostport:$contport --name ${env.APPLICATION_NAME}-$envdeploy ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}
        """
    }
  }
}
