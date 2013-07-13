#! /usr/bin/env jruby

Gem::Specification.new do |s|
  s.name        = 'jrjackson'
  s.version     = '0.1.2'
  s.date        = '2013-04-03'
  s.platform    = Gem::Platform::RUBY
  s.authors     = ['Guy Boertje']
  s.email       = ['guyboertje@gmail.com']
  s.homepage    = "http://github.com/guyboertje/jrjackson"
  s.summary     = %q{A JRuby wrapper for the java jackson json processor jar}
  s.description = %q{A mostly native JRuby wrapper for the java jackson json processor jar}

  s.add_development_dependency 'bundler', '~> 1.0'

  # = MANIFEST =
  s.files = %w[
    Gemfile
    README.md
    Rakefile
    benchmarking/.jrubyrc
    benchmarking/benchmark.rb
    jrjackson.gemspec
    lib/jrjackson.rb
    lib/jrjackson/jars/jrjackson-1.0.jar
    lib/jrjackson/jrjackson.rb
    lib/jrjackson/version.rb
    lib/require_relative_patch.rb
    pom.xml
    profiling/profiled.rb
    src/main/java/com/jrjackson/JrJacksonRaw.java
    src/main/java/com/jrjackson/JrJacksonService.java
    src/main/java/com/jrjackson/JrJacksonStr.java
    src/main/java/com/jrjackson/JrJacksonSym.java
    src/main/java/com/jrjackson/ParseError.java
    src/main/java/com/jrjackson/RubyObjectDeserializer.java
    src/main/java/com/jrjackson/RubyObjectSymDeserializer.java
  ]
  # = MANIFEST =

end
