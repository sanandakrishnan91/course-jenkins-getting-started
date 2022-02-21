pipeline {
    agent any
    triggers {
        cron('25 * * * *')
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/g0t4/jgsu-spring-petclinic.git', branch: 'main'
            }            
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
                //sh 'false' // true
            }
        
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
                changed {
                    emailext subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input',
                    body: 'Please go to ${BUILD_URL} and verify the build', 
                    attachLog: true, 
                    compressLog: true, 
                    to: "test@jenkins",
                    recipientProviders: [requestor(), upstreamDevelopers()]
                }
            }
        }
    }
}
