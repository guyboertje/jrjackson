package com.jrjackson;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.io.IOException;

@JRubyModule(name = "JrJacksonRaw")
public class JrJacksonRaw extends RubyObject {
  private static final ObjectMapper mapper = new ObjectMapper();

  public JrJacksonRaw(Ruby ruby, RubyClass metaclass) {
    super(ruby, metaclass);
  }

  @JRubyMethod(module = true, name = {"parse", "load"}, required = 1)
  public static IRubyObject parse(ThreadContext context, IRubyObject self, RubyString string) {
    Ruby ruby = context.getRuntime();
    try {
      Object o =  mapper.readValue(string.toString(), Object.class);
      return (RubyObject)JavaUtil.convertJavaToRuby(ruby, o);
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
