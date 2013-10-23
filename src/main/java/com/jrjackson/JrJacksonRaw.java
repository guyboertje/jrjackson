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
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

@JRubyModule(name = "JrJacksonRaw")
public class JrJacksonRaw extends RubyObject {
  private static final HashMap<String, ObjectMapper> mappers = new HashMap<String, ObjectMapper>(3);
  private static final HashMap<String, RubySymbol> symbols = new HashMap<String, RubySymbol>(3);

  private static final Ruby _ruby = Ruby.getGlobalRuntime();

  static {
    mappers.put("str",
      RubyJacksonModule.mappedAs("str")
    );
    mappers.put("sym",
      RubyJacksonModule.mappedAs("sym")
    );
    mappers.put("raw",
      RubyJacksonModule.mappedAs("raw")
    );
    symbols.put("sym",
      RubyUtils.rubySymbol(_ruby, "symbolize_keys")
    );
    symbols.put("raw",
      RubyUtils.rubySymbol(_ruby, "raw")
    );
    symbols.put("bigdecimal",
      RubyUtils.rubySymbol(_ruby, "use_bigdecimal")
    );
  }

  public JrJacksonRaw(Ruby ruby, RubyClass metaclass) {
    super(ruby, metaclass);
  }

  private static boolean flagged(RubyHash opts, String key) {
    Object val = opts.get(symbols.get(key));
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
    String key = "str";
    ObjectMapper local;

    if (opts != context.nil) {
      options = opts.convertToHash();
      if (flagged(options, "sym")) {
        key = "sym";
      }
      if (flagged(options, "raw")) {
        key = "raw";
      }
      local = mappers.get(key).copy();
      if (flagged(options, "bigdecimal")) {
        local.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
      }
      else {
        local.disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
      }
    }
    else {
      local = mappers.get(key).copy();
    }
    return _parse(context, arg, local);
  }

  @JRubyMethod(module = true, name = {"parse_raw", "load_raw"}, required = 1)
  public static IRubyObject parse_raw(ThreadContext context, IRubyObject self, IRubyObject arg)
    throws IOException
  {
    return _parse(context, arg, mappers.get("raw"));
  }

  @JRubyMethod(module = true, name = {"parse_sym", "load_sym"}, required = 1)
  public static IRubyObject parse_sym(ThreadContext context, IRubyObject self, IRubyObject arg)
    throws IOException
  {
    return _parse(context, arg, mappers.get("sym"));
  }

  @JRubyMethod(module = true, name = {"parse_str", "load_str"}, required = 1)
  public static IRubyObject parse_str(ThreadContext context, IRubyObject self, IRubyObject arg)
    throws IOException
  {
    return _parse(context, arg, mappers.get("str"));
  }

  public static IRubyObject _parse(ThreadContext context, IRubyObject arg, ObjectMapper mapper) 
    throws IOException
  {
    Ruby ruby = context.getRuntime();

    try {
      Object o;
      if (arg instanceof RubyString) {
        o = mapper.readValue(
          ((RubyString)arg).getBytes(), Object.class
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
  public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject arg)
    throws IOException, JsonProcessingException
  {
    Ruby ruby = context.getRuntime();
    Object obj = arg.toJava(Object.class);
      String s = mappers.get("raw").writeValueAsString(obj);
      return RubyUtils.rubyString(ruby, s);

    // try {
    //   String s = mappers.get("raw").writeValueAsString(obj);
    //   return RubyUtils.rubyString(ruby, s);
    // }
    // catch (JsonProcessingException e) {
    //   throw ParseError.newParseError(ruby, e.getLocalizedMessage());
    // }
    // catch (IOException e) {
    //   throw ruby.newIOError(e.getLocalizedMessage());
    // }
  }
}
