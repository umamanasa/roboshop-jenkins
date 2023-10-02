def call() {
    pipeline {
        agent any

        options {
            ansiColor('xterm')
        }

        parameters {
            string(name: 'COMPONENT', defaultValue: '', description: 'Which Component to Deploy?')
            string(name: 'VERSION', defaultValue: '', description: 'Which Version to Deploy?')
            string(name: 'ENV', defaultValue: '', description: 'Which Environment to Deploy?')
        }

        environment {
            SSH = credentials('centos-ssh')
        }

        stages {
            stage('Parameter Store Update') {
                steps {
                    sh '''
                      aws ssm put-parameter --name "${COMPONENT}.${ENV}.appVersion" --type "" --value "${VERSION}" --overwrite
'''
                    script {
                        addShortText(text: "${ENV}-${COMPONENT}-${VERSION}")
                    }
                }
            }
            stage('Deploy') {
                steps {
                    sh '''
                      aws ec2 describe-instances --filters "Name=tag:Name,Values=${ENV}-${COMPONENT}" --query 'Reservations[*].Instances[*].PrivateIpAddress' --output text >inv
                      ansible-playbook -i inv main.yml -e component=${COMPONENT} -e env=${ENV} -e ansible_user=${SSH_USR} -e ansible_password=${SSH_PSW}
'''
                }
            }
        }

        post {
            always {
                cleanWs()
            }
        }

    }
}