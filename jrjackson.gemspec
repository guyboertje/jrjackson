#! /usr/bin/env jruby

Gem::Specification.new do |s|
  s.name        = 'jrjackson'
  s.version     = '0.0.7'
  s.date        = '2012-03-14'
  s.platform    = Gem::Platform::RUBY
  s.authors     = ['Guy Boertje']
  s.email       = ['guyboertje@gmail.com']
  s.homepage    = "http://github.com/guyboertje/jrjackson"
  s.summary     = %q{A JRuby wrapper for the java jackson json processor jar}
  s.description = %q{}

  # = MANIFEST =
  s.files = %w[
    Gemfile
    README
    Rakefile
    benchmarking/benchmark.rb
    jrjackson.gemspec
    lib/jrjackson.rb
    lib/jrjackson/jackson-core-asl-1.9.5.jar
    lib/jrjackson/jackson-mapper-asl-1.9.5.jar
    lib/jrjackson/jackson-smile-1.9.5.jar
    lib/jrjackson/jrjackson.rb
    lib/jrjackson/rubify.rb
    lib/jrjackson/rubify_with_symbol_keys.rb
    lib/jrjackson/version.rb
    lib/jrjackson_r.rb
    lib/jrjackson_r_sym.rb
    profiling/profiled.rb
  ]
  # = MANIFEST =

  s.test_files    = `git ls-files -- {test,spec,features}/*`.split("\n")
  s.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }

  s.add_development_dependency  'awesome_print',      '~> 0.4.0'

end
