%W(jackson-core-asl-1.9.5.jar jackson-mapper-asl-1.9.5.jar jackson-smile-1.9.5.jar).each {|f| require File.join("jrjackson",f)}

module JrJackson
  include_package "org.codehaus.jackson"
  include_package "org.codehaus.jackson.map"
  include_package "org.codehaus.jackson.smile"

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
