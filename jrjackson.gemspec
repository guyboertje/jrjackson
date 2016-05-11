#! /usr/bin/env jruby
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)

require 'jrjackson/build_info'

Gem::Specification.new do |s|
  s.name        = 'jrjackson'
  s.version     = JrJackson::BuildInfo.version
  s.date        = JrJackson::BuildInfo.release_date
  # need java platform for jar-dependencies to work
  s.platform    = 'java'
  s.authors     = ['Guy Boertje']
  s.email       = ['guyboertje@gmail.com']
  s.homepage    = "http://github.com/guyboertje/jrjackson"
  s.summary     = %q{A JRuby wrapper for the java jackson json processor jar}
  s.description = %q{A mostly native JRuby wrapper for the java jackson json processor jar}
  s.license     = 'Apache License 2.0'

  s.add_development_dependency 'bundler', '~> 1.10'
  s.add_development_dependency 'jar-dependencies', '< 2.0', '>= 0.3.2'
  s.add_development_dependency 'ruby-maven', '~>3.3.10'

  JACKSON_VERSION = '2.7.3'
  s.requirements << "jar com.fasterxml.jackson.core:jackson-core, #{JACKSON_VERSION}"
  s.requirements << "jar com.fasterxml.jackson.core:jackson-annotations, #{JACKSON_VERSION}"
  s.requirements << "jar com.fasterxml.jackson.core:jackson-databind, #{JACKSON_VERSION}"
  s.requirements << "jar com.fasterxml.jackson.module:jackson-module-afterburner, #{JACKSON_VERSION}"

  s.files = JrJackson::BuildInfo.files

end
