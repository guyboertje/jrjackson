package com.jrjackson;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyModule;
import org.jruby.exceptions.RaiseException;
import org.jruby.ext.stringio.RubyStringIO;
import org.jruby.java.addons.IOJavaAddons;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.RubyDateFormat;

import java.io.InputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

@JRubyModule(name = "JrJacksonRaw")
public class JrJacksonRaw extends RubyObject {
  protected static ObjectMapper mapper_raw = new ObjectMapper();
  protected static ObjectMapper mapper_sym = new ObjectMapper();
  protected static ObjectMapper mapper_str = new ObjectMapper();
  private static Ruby __ruby__;

  {
    mapper_raw.registerModule(
      RubyJacksonModule.asRaw()
    ).registerModule(
      new AfterburnerModule()
    ).setDateFormat(new RubyDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US, true));
    
    mapper_sym.registerModule(
      RubyJacksonModule.asSym()
    ).registerModule(
      new AfterburnerModule()
    ).setDateFormat(new RubyDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US, true));
    
    mapper_str.registerModule(
      RubyJacksonModule.asStr()
    ).registerModule(
      new AfterburnerModule()
    ).setDateFormat(new RubyDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US, true));
  }

  public JrJacksonRaw(Ruby ruby, RubyClass metaclass) {
    super(ruby, metaclass);
    this.__ruby__ = ruby;
  }

  private static boolean flagged(RubyHash opts, RubySymbol key) {
    // if (opts == null) {
    //   return false;
    // }
    Object val = opts.get(key);
    if (val == null) {
      return false;
    }
    return (Boolean) val;
  }

  // deserialize
  @JRubyMethod(module = true, name = {"parse", "load"}, required = 2)
  public static IRubyObject parse(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts) 
    throws IOException
  {
    RubyHash options = null;
    Ruby ruby = context.getRuntime();
    ObjectMapper mapper = mapper_raw;

    if (opts != context.nil) {
      options = opts.convertToHash();
      if (flagged(options, RubyUtils.rubySymbol(ruby, "symbolize_keys"))) {
        mapper = mapper_sym;
      }
      if (flagged(options, RubyUtils.rubySymbol(ruby, "str"))) {
        mapper = mapper_str;
      }
      if (flagged(options, RubyUtils.rubySymbol(ruby, "use_bigdecimal"))) {
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
      }
      else {
        mapper.disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
      }
    }

    try {
      Object o;
      if (arg instanceof RubyString) {
        o = mapper.readValue(
          arg.toString(), Object.class
        );
      } else if ((arg instanceof RubyIO) || (arg instanceof RubyStringIO)) {
        IRubyObject stream = IOJavaAddons.AnyIO.any_to_inputstream(context, arg);
        o = mapper.readValue((InputStream)stream.toJava(InputStream.class), Object.class);
      } else {
        throw ruby.newArgumentError("Unsupported source. This method accepts String or IO");
      }
      return RubyUtils.rubyObject(ruby, o);
    }
    catch (JsonProcessingException e) {
      throw ParseError.newParseError(ruby, e.getLocalizedMessage());
    }
    catch (IOException e) {
      throw ruby.newIOError(e.getLocalizedMessage());
    }
  }

  // serialize
  @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1)
  public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject arg) {
    Ruby ruby = context.getRuntime();
    Object obj = arg.toJava(Object.class);
    try {
      String s = mapper_raw.writeValueAsString(obj);
      return RubyUtils.rubyString(ruby, s);
    }
    catch (JsonProcessingException e) {
      throw ParseError.newParseError(ruby, e.getLocalizedMessage());
    }
    catch (IOException e) {
      throw ruby.newIOError(e.getLocalizedMessage());
    }
  }
}
