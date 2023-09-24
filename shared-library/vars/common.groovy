def compile() {
  if( env.codeType == "python" || env.codeType == "static" ) {
    return "Return Do not need Compilation"
  }

  stage('Compile Code') {
    if( env.codeType == "maven" ) {
      sh '/home/centos/maven/bin/mvn package'
    }

    if( env.codeType == "nodejs" ) {
      sh 'npm install'
    }
  }
}

def test() {
  stage('Test Cases ') {
    if( env.codeType == "maven" ) {
      sh '/home/centos/maven/bin/mvn test'
    }

    if( env.codeType == "nodejs" ) {
      sh 'npm test'
    }
    if( env.codeType == "python" ) {
      sh 'python3.6 -m unittest'
    }
  }
}

def codeQuality() {
  stage('Code Quality') {
    env.sonaruser = sh (script: 'aws ssm get-parameter --name "sonarqube.user" --with-decryption --query="Parameter.Value" |xargs', returnStdout: true).trim()
    env.sonarpass = sh (script: 'aws ssm get-parameter --name "sonarqube.pass" --with-decryption --query="Parameter.Value" |xargs', returnStdout: true).trim()
    wrap([$class: "MaskPasswordsBuildWrapper", varPasswordPairs: [[password: sonarpass]]]) {

      if(env.codeType == "maven"){
        sh 'sonar-scanner -Dsonar.host.url=http://172.31.23.83:9000 -Dsonar.login=${sonaruser} -Dsonar.password=${sonarpass} -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true -Dsonar.javabinaries=./target'

      } else {
        sh 'sonar-scanner -Dsonar.host.url=http://172.31.23.83:9000 -Dsonar.login=${sonaruser} -Dsonar.password=${sonarpass} -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true'
      }
    }
  }
}

def CodeSecurity() {
  stage('Code Security') {
    print 'Code Security'

    // In Code Security we will generally use SAST & SCA Checks (CHECK MARX TOOL FOR CODE SECURITY)
    // You can say that your company is using checkmarxSAST and checkmarx SCA for code security
  }
}

def release() {
  stage('Release') {
    print 'Release'
  }
}