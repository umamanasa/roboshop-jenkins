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
    env.nexususer = sh(script: 'aws ssm get-parameter --name "nexus.username" --with-decryption --query="Parameter.Value" |xargs', returnStdout: true).trim()
    env.nexuspass = sh(script: 'aws ssm get-parameter --name "nexus.password" --with-decryption --query="Parameter.Value" |xargs', returnStdout: true).trim()
    wrap([$class: "MaskPasswordsBuildWrapper", varPasswordPairs: [[password: nexuspass]]]) {
      sh 'echo ${TAG_NAME} >VERSION'

      if (env.codeType == "nodejs") {
        sh 'zip -r ${component}-${TAG_NAME}.zip server.js node_modules VERSION ${schemadir}'
      } else if (env.codeType == "maven") {
        sh 'cp target/${component}-1.0.jar ${component}.jar; zip -r ${component}-${TAG_NAME}.zip ${component}.jar VERSION ${schemadir}'
      } else {
        sh 'zip -r ${component}-${TAG_NAME}.zip *'
      }

      sh 'curl -v -u ${nexususer}:${nexuspass} --upload-file ${component}-${TAG_NAME}.zip http://172.31.89.119:8081/repository/${component}/${component}-${TAG_NAME}.zip'
    }
  }
}
