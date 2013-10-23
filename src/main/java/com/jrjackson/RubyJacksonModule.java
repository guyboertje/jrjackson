package com.jrjackson;

import java.util.*;
import java.text.SimpleDateFormat;

import org.jruby.*;
// import org.jruby.util.RubyDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public class RubyJacksonModule extends SimpleModule
{

  private RubyJacksonModule()
  {
    super("JrJacksonStrModule", VersionUtil.versionFor(RubyJacksonModule.class));
  }

  public static ObjectMapper mappedAs(String key)
  {
    ObjectMapper mapper = new ObjectMapper();
    
    mapper.registerModule(
      new AfterburnerModule()
    );

    if (key == "sym") {
      mapper.registerModule(
        asSym()
      );
    }
    else if (key == "raw") {
      mapper.registerModule(
        asRaw()
      );
    }
    else {
      mapper.registerModule(
        asStr()
      );
    }
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));
    return mapper;
  }

  public static SimpleModule asRaw()
  { 
    return new RubyJacksonModule().addSerializer(
      RubyObject.class, RubyAnySerializer.instance
    );
  }

  public static SimpleModule asSym()
  { 
    return new RubyJacksonModule().addSerializer(
      RubyObject.class, RubyAnySerializer.instance
    ).addDeserializer(
      Object.class, new RubyObjectDeserializer().setSymbolStrategy()
    );
  }

  public static SimpleModule asStr()
  { 
    return new RubyJacksonModule().addSerializer(
      RubyObject.class, RubyAnySerializer.instance
    ).addDeserializer(
      Object.class, new RubyObjectDeserializer().setStringStrategy()
    );
  }
}
