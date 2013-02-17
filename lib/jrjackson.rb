unless RUBY_PLATFORM =~ /java/
  error "This library is only compatible with a java-based ruby environment like JRuby."
  exit 255
end

require 'java'

$CLASSPATH << 'jrjackson'

require File.expand_path File.join("lib", "jrjackson","jrjackson")
