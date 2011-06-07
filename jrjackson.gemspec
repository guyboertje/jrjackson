#! /usr/bin/env jruby

spec = Gem::Specification.new do |s|
  s.name = 'jrjackson'
  s.version = '0.0.6'
  s.authors = ['Guy Boertje']
  s.email = 'gboertje@gowebtop.com'
  s.date = '2011-04-25'
  s.summary = 'A JRuby wrapper for the java jackson json processor jar'
  s.description = s.summary
  s.homepage = nil
  s.require_path = 'lib'
  s.files = %W(lib/jrjackson.rb
                lib/jrjackson/jackson-core-lgpl-1.7.3.jar lib/jrjackson/jackson-mapper-lgpl-1.7.3.jar lib/jrjackson/jackson-smile-1.7.3.jar
                lib/jrjackson/jackson-core-lgpl-1.8.0.jar lib/jrjackson/jackson-mapper-lgpl-1.8.0.jar lib/jrjackson/jackson-smile-1.8.0.jar
                lib/jrjackson/jrjackson.rb lib/jrjackson/rubify.rb lib/jrjackson_r.rb
            )
  s.test_files = []
end
