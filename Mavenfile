#-*- mode: ruby -*-

VERSION='1.2.20'
gemspec :jar => "jrjackson/jars/jrjackson-#{VERSION}.jar"

# overwrite groupId:artifacgtId:version from gem
id "com.jrjackson.jruby:jrjackson:#{VERSION}"
packaging :jar


properties 'project.build.sourceEncoding' => 'UTF-8',
           'netbeans.hint.jdkPlatform' => 'JDK_1.8',
           # create a pom.xml from this here
           'polyglot.dump.pom' => 'pom.xml'

jar 'junit:junit', '4.11', :scope => :test

jar 'org.jruby:jruby', '9.1.5.0', :scope => :provided

plugin :compiler, '3.1', :source => '1.7', :target => '1.7',
       :showDeprecation => false,
       :showWarnings => false,
       :executable => '${JAVA_HOME}/bin/javac',
       :fork => true

plugin :surefire, '2.17', :skipTests => true

# since bundle install does not vendor our jars we need to it manually
plugin 'org.torquebox.mojo:jruby9-exec-maven-plugin', '0.3.1' do
  execute_goal :exec, :id => 'vendor-jars', :phase => 'prepare-package',
               :script => "require 'jars/installer';Jars::Installer.vendor_jars!"
end
