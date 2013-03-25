package com.jrjackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.javasupport.util.RuntimeHelpers;


public class RubyObjectSymDeserializer
    // extends RubyObjectDeserializer<RubyObject>
    extends RubyObjectDeserializer
{
  
  protected final static Ruby __ruby__ = Ruby.getGlobalRuntime();

  public final static RubyObjectSymDeserializer instance = new RubyObjectSymDeserializer();
  
  public RubyObjectSymDeserializer() { super(); }

  /*
  /**********************************************************
  /* Internal methods
  /**********************************************************
   */
  
  protected RubySymbol rubySymbol(JsonParser jp) throws IOException {
    return __ruby__.newSymbol(jp.getText());
  }

  protected RubyObject mapObject(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException
  {
    JsonToken t = jp.getCurrentToken();
    if (t == JsonToken.START_OBJECT) {
        t = jp.nextToken();
    }
    // 1.6: minor optimization; let's handle 1 and 2 entry cases separately
    if (t != JsonToken.FIELD_NAME) { // and empty one too
        // empty map might work; but caller may want to modify... so better just give small modifiable
        return RubyHash.newHash(__ruby__);
    }
    RubySymbol field1 = rubySymbol(jp);
    jp.nextToken();
    RubyObject value1 = deserialize(jp, ctxt);
    if (jp.nextToken() != JsonToken.FIELD_NAME) { // single entry; but we want modifiable
        return RuntimeHelpers.constructHash(__ruby__, field1, value1);
    }
    RubySymbol field2 = rubySymbol(jp);
    jp.nextToken();
    RubyObject value2 = deserialize(jp, ctxt);
    if (jp.nextToken() != JsonToken.FIELD_NAME) {
        return RuntimeHelpers.constructHash(__ruby__, field1, value1, field2, value2);
    }
    RubyHash result = RuntimeHelpers.constructHash(__ruby__, field1, value1, field2, value2);
    do {
        RubySymbol fieldName = rubySymbol(jp);
        jp.nextToken();
        result.fastASet(fieldName, deserialize(jp, ctxt));
    } while (jp.nextToken() != JsonToken.END_OBJECT);
    return result;
  }
}

