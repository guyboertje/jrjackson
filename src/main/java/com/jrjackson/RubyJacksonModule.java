package com.jrjackson;

import java.io.IOException;
import java.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jruby.*;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.ext.bigdecimal.RubyBigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;

import com.fasterxml.jackson.databind.node.*;

public class RubyJacksonModule extends SimpleModule
{

  private RubyJacksonModule()
  {
    super("JrJacksonStrModule", VersionUtil.versionFor(RubyJacksonModule.class));
  }

  public static Module asRaw()
  { 
    return new RubyJacksonModule().addSerializer(
      RubySymbol.class, ToStringSerializer.instance
    );
  }

  public static Module asSym()
  { 
    return new RubyJacksonModule().addSerializer(
      RubySymbol.class, ToStringSerializer.instance
    ).addDeserializer(
      Object.class, RubyObjectSymDeserializer.instance
    );
  }

  public static Module asStr()
  { 
    // .addKeyDeserializer(RubySymbol.class, RubySymbolKeyDeserializer.instance);
    return new RubyJacksonModule().addSerializer(
      RubySymbol.class, ToStringSerializer.instance
    ).addDeserializer(
      Object.class, RubyObjectStrDeserializer.instance
    );
  }
}
