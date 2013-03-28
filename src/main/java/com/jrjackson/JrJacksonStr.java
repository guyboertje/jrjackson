package com.jrjackson;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;

@JRubyModule(name = "JrJacksonStr")
public class JrJacksonStr extends RubyObject {
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    SimpleModule _module = new SimpleModule("JrJacksonStrModule", new Version(1, 0, 0, null));
    _module.addDeserializer(RubyObject.class, RubyObjectDeserializer.instance);
    mapper.registerModule(_module);
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    mapper.setDateFormat(sdf);
  }

  public JrJacksonStr(Ruby ruby, RubyClass metaclass) {
    super(ruby, metaclass);
  }

  @JRubyMethod(module = true, name = {"parse", "load"}, required = 1)
  public static IRubyObject parse(ThreadContext context, IRubyObject self, RubyString string) {
    Ruby ruby = context.getRuntime();
    String str = string.toString();
    try {
      return mapper.readValue(str, RubyObject.class);
    }
    catch (JsonProcessingException e) {
      throw ParseError.newParseError(ruby, e.getLocalizedMessage());
    }
    catch (IOException e) {
      throw ruby.newIOError(e.getLocalizedMessage());
    }
  }

  @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1)
  public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject object) {
    Ruby ruby = context.getRuntime();
    Object obj = object.toJava(Object.class);
    try {
      String s = mapper.writeValueAsString(obj);
      return ruby.newString(s);
    }
    catch (JsonProcessingException e) {
      throw ParseError.newParseError(ruby, e.getLocalizedMessage());
    }
    catch (IOException e) {
      throw ruby.newIOError(e.getLocalizedMessage());
    }
  }
}
