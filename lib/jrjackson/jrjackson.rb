%W(jackson-core-lgpl-1.7.3.jar jackson-mapper-lgpl-1.7.3.jar jackson-smile-1.7.3.jar).each {|f| require File.join("jrjackson",f)}
#jackson-smile-1.8.0.jar
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
  end
  module Smile
    def self.parse(smile_bytes)
      SmileMapper.read_value smile_bytes,0,smile_bytes.size, Jclass
    end
    def self.generate(object)
      SmileMapper.writeValueAsBytes(object)
    end
  end
end

JSON = JrJackson::Json
