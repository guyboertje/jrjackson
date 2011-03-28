module JrJackson
  include_package "org.codehaus.jackson.map.module"
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

  _module = SimpleModule.new("JrJacksonModule", Version.new(1, 0, 0, nil))
  _module.addDeserializer(Jclass, RubyObjectDeserializer.new)
  Mapper.registerModule(_module)

end

