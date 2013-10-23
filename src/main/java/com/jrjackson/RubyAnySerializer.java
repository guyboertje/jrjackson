package com.jrjackson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import org.jruby.*;

public class RubyAnySerializer extends StdSerializer<RubyObject>
{
  /**
   * Singleton instance to use.
   */
  public static final RubyAnySerializer instance = new RubyAnySerializer();
  private static HashMap<Class, Class> class_maps = new HashMap<Class, Class>();

  static {
    class_maps.put(RubyBoolean.class, Boolean.class);
  }

  public RubyAnySerializer() { super(RubyObject.class); }

  private Class<?> rubyJavaClassLookup(Class target)
  {
    Class<?> val = class_maps.get(target);
    if (val == null) {
      return Object.class;
    }
    return val;
  }

  @Override
  public void serialize(RubyObject value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException, JsonGenerationException
  {
    if (value instanceof RubySymbol || value instanceof RubyString) {
      jgen.writeString(value.toString());
    } else if (value instanceof RubyHash) {
      provider.findTypedValueSerializer(Map.class, true, null).serialize(value, jgen, provider);
    } else if (value instanceof RubyArray) {
      provider.findTypedValueSerializer(List.class, true, null).serialize(value, jgen, provider);
    } else {
      Object val = value.toJava(rubyJavaClassLookup(value.getClass()));
      if ( val instanceof RubyObject) {
        throw new JsonGenerationException("Cannot find Serializer for class: " + val.getClass().getName());
      } else {
        provider.defaultSerializeValue(val, jgen);
      }
    }
  }

  /**
   * Default implementation will write type prefix, call regular serialization
   * method (since assumption is that value itself does not need JSON
   * Array or Object start/end markers), and then write type suffix.
   * This should work for most cases; some sub-classes may want to
   * change this behavior.
   */
  @Override
  public void serializeWithType(RubyObject value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
    throws IOException, JsonGenerationException
  {
    typeSer.writeTypePrefixForScalar(value, jgen);
    serialize(value, jgen, provider);
    typeSer.writeTypeSuffixForScalar(value, jgen);
  }
}
