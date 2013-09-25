unless RUBY_PLATFORM =~ /java/
  puts "This library is only compatible with a java-based ruby environment like JRuby."
  exit 255
end

require_relative "jars/jrjackson-1.1.3.jar"
require 'com/jrjackson/jr_jackson'

module JrJackson
  module Json
      class << self
      TIME_REGEX = %r(\A(\d{4}-\d\d-\d\d|(\w{3}\s){2}\d\d)\s\d\d:\d\d:\d\d)
      def load(json_string, options = nil)


        # mod = if options[:raw]
        #   JrJackson::Raw
        # elsif options[:symbolize_keys]
        #   JrJackson::Sym
        # else
        #   JrJackson::Str
        # end
        # if json_string.is_a?(String) && json_string =~ TIME_REGEX
        #   mod.parse("\"#{json_string}\"")
        # else
        #   mod.parse(json_string)
        # end

        if json_string.is_a?(String) && json_string =~ TIME_REGEX
          JrJackson::Raw.parse("\"#{json_string}\"", nil)
        else
          JrJackson::Raw.parse(json_string, options)
        end
      end

      def dump(object)
        case object
        when Array, Hash, String
          JrJackson::Raw.generate(object)
        when true, false
          object.to_s
        when nil
          'null'
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
