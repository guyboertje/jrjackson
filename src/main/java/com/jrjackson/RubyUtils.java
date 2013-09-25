package com.jrjackson;

import java.io.IOException;
import java.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jruby.*;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.ext.bigdecimal.RubyBigDecimal;

public class RubyUtils
{
  
  public static RubyObject rubyObject(Ruby ruby, Object node)
  {
    return (RubyObject)JavaUtil.convertJavaToRuby(ruby, node);
  }

  public static RubyString rubyString(Ruby ruby, String node)
  {
    return ruby.newString(node);
  }

  public static RubySymbol rubySymbol(Ruby ruby, String node)
  {
    return RubySymbol.newSymbol(ruby, node);
  }

  public static RubyArray rubyArray(Ruby ruby, Object[] arg)
  {
    return (RubyArray)JavaUtil.convertJavaToRuby(ruby, arg);
  }

  public static RubyArray rubyArray(Ruby ruby, List arg)
  {
    return (RubyArray)JavaUtil.convertJavaToRuby(ruby, arg);
  }
  
  public static RubyHash rubyHash(Ruby ruby, Map arg)
  {
    return (RubyHash)JavaUtil.convertJavaToRuby(ruby, arg);
  }

  public static RubyFixnum rubyFixnum(Ruby ruby, Integer arg)
  {
    return ruby.newFixnum(arg);
  }

  public static RubyFixnum rubyFixnum(Ruby ruby, Long arg)
  {
    return ruby.newFixnum(arg);
  }

  public static RubyBignum rubyBignum(Ruby ruby, BigInteger arg)
  {
    return RubyBignum.newBignum(ruby, arg);
  }

  public static RubyFloat rubyFloat(Ruby ruby, Double arg)
  {
    return ruby.newFloat(arg);
  }

  public static RubyBigDecimal rubyBigDecimal(Ruby ruby, BigDecimal arg)
  {
    return new RubyBigDecimal(ruby, arg);
  }

  public static RubyBoolean rubyBoolean(Ruby ruby, Boolean arg)
  {
    return ruby.newBoolean(arg);
  }
}
