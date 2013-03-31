unless RUBY_PLATFORM =~ /java/
  puts "This library is only compatible with a java-based ruby environment like JRuby."
  exit 255
end

require_relative "jars/jrjackson-1.0.jar"
require 'com/jrjackson/jr_jackson'

module JrJackson
  module Json
      class << self
      TIME_REGEX = %r(\A\d{4}-\d\d-\d\d\s\d\d:\d\d:\d\d)
      def load(json_string, options = {})
        mod = if options[:raw]
          JrJackson::Raw
        elsif options[:symbolize_keys]
          JrJackson::Sym
        else
          JrJackson::Str
        end
        if json_string.is_a?(String) && json_string =~ TIME_REGEX
          mod.parse("\"#{json_string}\"")
        else
          mod.parse(json_string)
        end
      end

      def dump(object)
        case object
        when Array, Hash
          JrJackson::Raw.generate(object)
        when String
          "\"#{object}\""
        when nil, true, false
          object
        else
          if object.respond_to?(:to_json)
            object.to_json
          elsif object.respond_to?(:to_s)
            object.to_s
          else
            object
          end
        end
      end

      alias :parse :load
      alias :generate :dump
    end
  end
end

JSON = JrJackson::Json unless defined?(JSON)
