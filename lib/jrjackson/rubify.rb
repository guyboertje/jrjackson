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
        k = jp.getText
        jp.nextToken
        v = deserialize(jp, ctxt)
        tmp.push(k)
        tmp.push(v)
        tmp.push(k.to_sym)
        tmp.push(v)
      end while jp.nextToken != JsonToken::END_OBJECT
      Hash[*tmp]
    end
  end

  _module = SimpleModule.new("JrJacksonModule", Version.new(1, 0, 0, nil))
  _module.addDeserializer(Jclass, RubyObjectDeserializer.new)
  Mapper.registerModule(_module)

end

