// Test file
pipeline {
    agent {
        label 'maven-slave'
    } 
    environment {
        APPLICATION_NAME = "eureka"
    }
    stages {
        stage('Build') {
            // Build happens here 
            // Only build should happen, no tests should be available
            steps {
                echo "Building the ${env.APPLICATION_NAME} application"
                // maven build should happpen here 
                sh "mvn clean package -DskipTests=true"
            }
        }
    }
}