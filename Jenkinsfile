/* @Library ("com.i27academy.slb@master") _
jfrogPipeline(
    appName: 'eureka'
) */

//This jenkins file is for eureka deploy

pipeline{
    agent{
        label 'k8s-slave'
    }
    environment{
        APPLICATION_NAME = 'eureka'
        POM_VERSION  = readMavenPom().getVersion()
        POM_PACKAGING = readMavenPom().getPackaging()
    }
    tools{
        maven 'maven-3.8.8'
        jdk  'Jdk17'
    }
  stages {
    //application build happens here
    stage ('build')
     {
      steps{
         // if env varible which we are calling is built in jenkins variable , no need to write env.varbilename , we can call it directly 
        echo " building the ${env.APPLICATION_NAME} application"
        sh "mvn clean package -D skipTests=true"


       }
      } 
    stage ('unit test')
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
    stage ('docker format')
     {
      steps{


        //  how to read the pom.xml 

        echo "ACTUAL_FORMAT : ${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING}"


        //need to have below formating way 
        //eureka-buildnumber-branchname.packaging

        echo "CUSTOM_FORMAT : ${APPLICATION_NAME}-${currentBuild.number}-${BRANCH_NAME}.${POM_PACKAGING}"
      }
     } 
    stage ('docker Build')
    {
      steps{
        sh """
            ls -la
            
             cp "${workspace}/target/i27-${APPLICATION_NAME}-${POM_VERSION}.${POM_PACKAGING} ./.cicd"

             ls -la ./.cicd
        """
        // /home/ansible/jenkins/workspace/i27-eureka_master/target/i27-eureka-0.0.1-SNAPSHOT.jar
      }
    }
  
  
  } 
  } 
