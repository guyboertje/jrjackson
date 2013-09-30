#! /usr/bin/env jruby

Gem::Specification.new do |s|
  s.name        = 'jrjackson'
  s.version     = '0.2.0'
  s.date        = '2013-09-30'
  s.platform    = Gem::Platform::RUBY
  s.authors     = ['Guy Boertje']
  s.email       = ['guyboertje@gmail.com']
  s.homepage    = "http://github.com/guyboertje/jrjackson"
  s.summary     = %q{A JRuby wrapper for the java jackson json processor jar}
  s.description = %q{A mostly native JRuby wrapper for the java jackson json processor jar}

  s.add_development_dependency 'bundler', '~> 1.0'

  # = MANIFEST =
  s.files = %w[
    .jrubyrc
    Gemfile
    README.md
    Rakefile
    benchmarking/.jrubyrc
    benchmarking/benchmark.rb
    benchmarking/benchmark_threaded.rb
    dependency-reduced-pom.xml
    jrjackson.gemspec
    lib/jrjackson.rb
    lib/jrjackson/jars/jrjackson-1.2.2.jar
    lib/jrjackson/jrjackson.rb
    lib/jrjackson/version.rb
    lib/require_relative_patch.rb
    pom.xml
    profiling/profiled.rb
    src/main/java/com/jrjackson/JrJacksonRaw.java
    src/main/java/com/jrjackson/JrJacksonService.java
    src/main/java/com/jrjackson/ParseError.java
    src/main/java/com/jrjackson/RubyJacksonModule.java
    src/main/java/com/jrjackson/RubyObjectDeserializer.java
    src/main/java/com/jrjackson/RubyObjectStrDeserializer.java
    src/main/java/com/jrjackson/RubyObjectSymDeserializer.java
    src/main/java/com/jrjackson/RubyUtils.java
    src/test/java/com/jrjackson/jruby/AppTest.java
    test/jrjackson_test.rb
  ]
  # = MANIFEST =

end
