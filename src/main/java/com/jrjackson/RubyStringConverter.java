package com.jrjackson;

import com.fasterxml.jackson.core.*;

import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyObject;

public class RubyStringConverter implements RubyKeyConverter
{
  public RubyObject convert(JsonParser jp) throws IOException {
    return RubyUtils.rubyString(Ruby.getGlobalRuntime(), jp.getText());
  }
}

