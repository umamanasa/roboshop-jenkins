def compile() {
  if( env.codeType == "maven" ) {
    print 'Maven'
  }

  if( env.codeType == "nodejs" ) {
    print 'NodeJS'
  }

  if( env.codeType == "python" ) {
    print 'Python'
  }

  if( env.codeType == "static" ) {
    print 'Static'
  }
}