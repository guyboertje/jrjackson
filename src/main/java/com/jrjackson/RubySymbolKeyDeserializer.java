package com.jrjackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.jruby.Ruby;
import org.jruby.RubySymbol;

final class RubySymbolKeyDeserializer extends StdKeyDeserializer
{
  private static final long serialVersionUID = 1L;

  public final static RubySymbolKeyDeserializer instance = new RubySymbolKeyDeserializer();

  RubySymbolKeyDeserializer() { super(RubySymbol.class); }

  @Override
  public RubySymbol _parse(String key, DeserializationContext ctxt) throws JsonMappingException
  {
    return RubyUtils.rubySymbol(Ruby.getGlobalRuntime(), key);
  }
}

