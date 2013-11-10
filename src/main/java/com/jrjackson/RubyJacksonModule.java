package com.jrjackson;

import java.util.*;
import java.text.SimpleDateFormat;

import org.jruby.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public class RubyJacksonModule extends SimpleModule
{

  private static final ObjectMapper static_mapper = new ObjectMapper().registerModule(
    new RubyJacksonModule().addSerializer(RubyObject.class, RubyAnySerializer.instance)
  );

  static {
    static_mapper.registerModule(new AfterburnerModule());
    static_mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    static_mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));
  }

  private RubyJacksonModule()
  {
    super("JrJacksonStrModule", VersionUtil.versionFor(RubyJacksonModule.class));
  }

  public static ObjectMapper mappedAs(String key, Ruby ruby)
  {
    if (key == "raw") {
      return static_mapper;
    }

    ObjectMapper mapper = new ObjectMapper().registerModule(
      new AfterburnerModule()
    );

    if (key == "sym") {
      mapper.registerModule(
        asSym(ruby)
      );
    }
    else {
      mapper.registerModule(
        asStr(ruby)
      );
    }
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));
    return mapper;
  }

  // public static SimpleModule asRaw()
  // { 
  //   return static_mapper;
  // }

  public static SimpleModule asSym(Ruby ruby)
  { 
    return new RubyJacksonModule().addSerializer(
      RubyObject.class, RubyAnySerializer.instance
    ).addDeserializer(
      Object.class, new RubyObjectDeserializer().withRuby(ruby).setSymbolStrategy()
    );
  }

  public static SimpleModule asStr(Ruby ruby)
  { 
    return new RubyJacksonModule().addSerializer(
      RubyObject.class, RubyAnySerializer.instance
    ).addDeserializer(
      Object.class, new RubyObjectDeserializer().withRuby(ruby).setStringStrategy()
    );
  }
}
