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
    }
  stages{
    //application build happens here
    stage ('build')
      steps{
         // if env varible which we are calling is built in jenkins variable , no need to write env.varbilename , we can call it directly 
     echo " building the ${env.APPLICATION_NAME} application"
      sh "mvn clean package"
    }
  }  
}
