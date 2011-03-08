require 'java'
require 'jackson-core-lgpl-1.7.3.jar'
require 'jackson-mapper-lgpl-1.7.3.jar'
require 'jackson-smile-1.7.3.jar'

module JrJackson
  include_package "org.codehaus.jackson.smile"
  include_package "org.codehaus.jackson.map"
  include_package "org.codehaus.jackson.map.module"
  include_package "org.codehaus.jackson"

  java_import org.codehaus.jackson.map.deser.UntypedObjectDeserializer
  java_import org.codehaus.jackson.Version
  java_import org.codehaus.jackson.JsonToken

  class RubyObjectDeserializer < UntypedObjectDeserializer
    def mapArray(jp,ctxt)
      arr = super(jp,ctxt)
      arr.entries
    end

    def mapObject(jp,ctxt)
      t = jp.getCurrentToken
      t = jp.nextToken if t == JsonToken::START_OBJECT

      return {} if t != JsonToken::FIELD_NAME
      tmp = []
      begin
        tmp.push(jp.getText)
        jp.nextToken
        tmp.push(deserialize(jp, ctxt))
      end while jp.nextToken != JsonToken::END_OBJECT
      Hash[*tmp]
    end
  end

  Jclass = java.lang.Object.java_class

  Mapper = ObjectMapper.new

  #_module = SimpleModule.new("MyModule", Version.new(1, 0, 0, nil))
  #_module.addDeserializer(Jclass, RubyObjectDeserializer.new)
  #Mapper.registerModule(_module)

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

