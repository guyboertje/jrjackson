package com.jrjackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;

import org.jruby.Ruby;

import org.jruby.RubyObject;

public class RubyObjectSymDeserializer
    extends RubyObjectDeserializer
{
  
  public final static RubyObjectSymDeserializer instance = new RubyObjectSymDeserializer();
  
  public RubyObjectSymDeserializer() { super(); }

  protected RubyObject convertKey(JsonParser jp) throws IOException {
    return RubyUtils.rubySymbol(Ruby.getGlobalRuntime(), jp.getText());
  }
}
