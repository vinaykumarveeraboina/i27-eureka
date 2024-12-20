/* @Library ("com.i27academy.slb@master") _
jfrogPipeline(
    appName: 'eureka'
) */

//This jenkins file is for eureka deploy

pipeline{

    agent{
        label 'k8s-slave'
    }
    options { 
      // Discard old builds
      
      buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '5'))
      
      }
    environment{
        DOCKERHUB = "docker.io/vinaykumarveeraboina"
        APPLICATION_NAME = 'eureka'
        POM_VERSION  = readMavenPom().getVersion()
        POM_PACKAGING = readMavenPom().getPackaging()
        DOCKER_CREDS = credentials('DokcerHub')
        SONAR_URL = 'http://34.122.118.251:9000'
        
        SONAR_TOKEN = credentials('sonar-secret')
    }
    tools{
        maven 'maven-3.8.8'
        jdk  'Jdk17'
    }
  stages {
    //application build happens here
    stage ('Build')
     {
      steps{
         // if env varible which we are calling is built in jenkins variable , no need to write env.varbilename , we can call it directly 
        echo " building the ${env.APPLICATION_NAME} application"
        sh "mvn clean package -D skipTests=true"


       }
      } 
    stage ('Unit-Test')
    {
      steps{
         // if env varible which we are calling is built in jenkins variable , no need to write env.varbilename , we can call it directly 
        echo " Tesitng the ${env.APPLICATION_NAME} application"
        sh "mvn test"
        }
        post{
          always{
            junit 'target/surefire-reports/*.xml'
          }
        }
    }
     stage ('Sonar _Test')
      {
      
          steps {
              echo " *************************      STARTING SONAR ANALYSIS with Quality gate     ************************ "
              withSonarQubeEnv('SonarQube') 
              {
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

    stage ('Docker-Format')
     {
      steps{


        //  how to read the pom.xml 

        echo "ACTUAL_FORMAT : ${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING}"


        //need to have below formating way 
        //eureka-buildnumber-branchname.packaging

        echo "CUSTOM_FORMAT : ${APPLICATION_NAME}-${currentBuild.number}-${BRANCH_NAME}.${POM_PACKAGING}"
      }
     } 
    stage ('Docker Build and Push ')
    {
      steps{
        sh """
            ls -la
            
             cp ${workspace}/target/i27-${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING} ./.cicd

             ls -la ./.cicd

             echo "***********************  Building Docker Image  *********************************"
             
             
            docker build --force-rm --no-cache --pull --rm=true --build-arg JAR_SOURCE=i27-${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING} -t ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT} ./.cicd 
             docker images 
             echo " *****************   Docker login ************************ "

              docker login -u ${DOCKER_CREDS_USR} -p ${DOCKER_CREDS_PSW}

             echo " ********************* Docker push ************************************* "

              docker push ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}

        """
        // /home/ansible/jenkins/workspace/i27-eureka_master/target/i27-eureka-0.0.1-SNAPSHOT.jar
      }
    }
    stage ("Docker deploy to DEV ")
    {
     steps{
        script{
        DockerDeploy('dev','5761','8761').call()
        }
        }
    }
  
   stage ("Docker deploy to TEST env ")
    {
     steps{
        
        script{
        DockerDeploy('test','6761','8761').call()

         }
      }
     }

   stage ("Docker deploy to STAGE env ")
    {
     steps{
        
        script{
        DockerDeploy('stage','7761','8761').call()

        }
       }
    } 
  } 
}
  


// this method is developed for deployning our app in different env 

def DockerDeploy(envdeploy,hostport,contport)

{

  echo "************************  Deplpoying to Docker $envdeploy  ********************************"


        withCredentials([usernamePassword(credentialsId: 'DockerHost', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]){
        
         
      
            echo " ******************   PULLING the container from docker hub ********************  "
           sh """
           sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker pull ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}
          
           """
          script{
          try {

           echo " ******************   stopping  the container     ********************  "
           sh """
           sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker stop ${env.APPLICATION_NAME}-t$envdeploy
          
           """

           echo " ******************   removing  the container  ********************  "
           sh """
           sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker rm  ${env.APPLICATION_NAME}-$envdeploy
          
           """
          }

          catch(err)
          {
            echo " caught the Error is ${err}"
          }
          }

            echo " ****************  runnng the container ***************** "

           sh """
           sshpass -p ${env.PASSWORD} ssh -o StrictHostKeyChecking=no ${env.USERNAME}@${env.docker_dev_server} docker run -d -p $hostport:$contport --name ${env.APPLICATION_NAME}-$envdeploy  ${env.DOCKERHUB}/${env.APPLICATION_NAME}:${GIT_COMMIT}

          """
        }
}

 
