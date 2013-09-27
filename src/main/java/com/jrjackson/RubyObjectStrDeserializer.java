package com.jrjackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;

import org.jruby.Ruby;

import org.jruby.RubyObject;

public class RubyObjectStrDeserializer
    extends RubyObjectDeserializer
{
  
  public final static RubyObjectStrDeserializer instance = new RubyObjectStrDeserializer();
  
  public RubyObjectStrDeserializer() { super(); }

  protected RubyObject convertKey(JsonParser jp) throws IOException {
    return RubyUtils.rubyString(Ruby.getGlobalRuntime(), jp.getText());
  }
}
