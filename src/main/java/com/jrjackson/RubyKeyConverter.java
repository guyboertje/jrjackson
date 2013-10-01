package com.jrjackson;

import com.fasterxml.jackson.core.*;

import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyObject;

public interface RubyKeyConverter
{
  public RubyObject convert(JsonParser jp) throws IOException;
}
