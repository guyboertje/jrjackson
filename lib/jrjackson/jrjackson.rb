%W(jackson-core-2.1.3.jar jackson-annotations-2.1.2.jar jackson-databind-2.1.3.jar jackson-dataformat-smile-2.1.3.jar).each {|f| require File.expand_path File.join("lib", "jrjackson",f)}
# %W(jackson-core-asl-1.9.5.jar jackson-mapper-asl-1.9.5.jar jackson-smile-1.9.5.jar).each {|f| require File.join("jrjackson",f)}

module JrJackson
  include_package "com.fasterxml.jackson.core"
  include_package "com.fasterxml.jackson.databind"
  include_package "com.fasterxml.jackson.dataformat.smile"

  # include_package 'com.fasterxml.jackson.core'
  # include_package 'com.fasterxml.jackson.core.io'
  # include_package 'com.fasterxml.jackson.core.json'
  # include_package 'com.fasterxml.jackson.core.util'
  # include_package 'com.fasterxml.jackson.core.base'
  # include_package 'com.fasterxml.jackson.core.sym'
  # include_package 'com.fasterxml.jackson.core.type'
  # include_package 'com.fasterxml.jackson.core.format'


  # include_package 'com.fasterxml.jackson.databind'
  # include_package 'com.fasterxml.jackson.databind.annotation'
  # include_package 'com.fasterxml.jackson.databind.cfg'
  # include_package 'com.fasterxml.jackson.databind.deser'
  # include_package 'com.fasterxml.jackson.databind.deser.impl'
  # include_package 'com.fasterxml.jackson.databind.deser.std'
  # include_package 'com.fasterxml.jackson.databind.exc'
  # include_package 'com.fasterxml.jackson.databind.ext'
  # include_package 'com.fasterxml.jackson.databind.introspect'
  # include_package 'com.fasterxml.jackson.databind.jsonschema'
  # include_package 'com.fasterxml.jackson.databind.jsonFormatVisitors'
  # include_package 'com.fasterxml.jackson.databind.jsontype'
  # include_package 'com.fasterxml.jackson.databind.jsontype.impl'
  # include_package 'com.fasterxml.jackson.databind.module'
  # include_package 'com.fasterxml.jackson.databind.node'
  # include_package 'com.fasterxml.jackson.databind.ser'
  # include_package 'com.fasterxml.jackson.databind.ser.impl'
  # include_package 'com.fasterxml.jackson.databind.ser.std'
  # include_package 'com.fasterxml.jackson.databind.type'
  # include_package 'com.fasterxml.jackson.databind.util'


  Jclass = java.lang.Object.java_class

  Mapper = ObjectMapper.new
  SmileMapper = ObjectMapper.new(SmileFactory.new)

  module Json
    def self.parse(json_string)
      Mapper.read_value json_string, Jclass
    end
    def self.generate(object)
      Mapper.writeValueAsString(object)
    end
    def self.load(json_string)
      Mapper.read_value json_string, Jclass
    end
    def self.dump(object)
      Mapper.writeValueAsString(object)
    end
  end
  module Smile
    def self.parse(smile_bytes)
      SmileMapper.read_value smile_bytes,0,smile_bytes.size, Jclass
    end
    def self.generate(object)
      SmileMapper.writeValueAsBytes(object)
    end
    def self.load(smile_bytes)
      SmileMapper.read_value smile_bytes,0,smile_bytes.size, Jclass
    end
    def self.dump(object)
      SmileMapper.writeValueAsBytes(object)
    end
  end
end

JSON = JrJackson::Json unless defined?(JSON)
