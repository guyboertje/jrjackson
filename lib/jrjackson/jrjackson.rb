unless RUBY_PLATFORM =~ /java/
  puts "This library is only compatible with a java-based ruby environment like JRuby."
  exit 255
end

require_relative "jars/jrjackson-1.0.jar"
require 'com/jrjackson/jr_jackson'

module JrJackson
  module Json
      class << self
      def parse(json_string, options = {})
        mod = if options[:raw]
          JrJackson::Raw
        elsif options[:symbolize_keys]
          JrJackson::Sym
        else
          JrJackson::Str
        end
        mod.parse(json_string)
      end

      def generate(object)
        JrJackson::Raw.generate(object)
      end

      alias :load :parse
      alias :dump :generate
    end
  end
end

JSON = JrJackson::Json unless defined?(JSON)
