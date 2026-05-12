#-*- mode: ruby -*-

load File.expand_path('lib/jrjackson/build_info.rb', File.dirname(__FILE__))
VERSION = ::JrJackson::BuildInfo.jar_version
gemspec :jar => "jrjackson/jars/jrjackson-#{VERSION}.jar"

# overwrite groupId:artifacgtId:version from gem
id "com.jrjackson.jruby:jrjackson:#{VERSION}"
packaging :jar

properties 'project.build.sourceEncoding' => 'UTF-8',
           # create a pom.xml from this here
           'polyglot.dump.pom' => 'pom.xml',
           'maven.compiler.release' => '11'

jar 'junit:junit', '4.13.2', :scope => :test

jar 'org.jruby:jruby', '9.4.14.0', :scope => :provided

plugin :compiler, '3.11.0', :release => '11',
       :showDeprecation => false,
       :showWarnings => false

plugin :surefire, '2.17', :skipTests => false
