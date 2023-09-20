def info(message) {
    echo "INFO: ${message}"
}

def warning(message) {
    echo "WARNING: ${message}"
}

def call() {
    pipeline {
        agent any

        stages {
            stage('Compile') {
                steps {
                    echo 'Hello World'
                    script {
                        info 'Starting'
                        warning 'Nothing to do!'
                    }
                }
            }

            stage('Test') {
                steps {
                    echo 'Hello World'
                }
            }

            stage('Code Quality') {
                steps {
                    echo 'Hello World'
                }
            }

            stage('Code Security') {
                steps {
                    echo 'Hello World'
                }
            }

            stage('App Deploy') {
                steps {
                    echo 'Hello World'
                }
            }
        }
    }
}