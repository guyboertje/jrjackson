package com.jrjackson;

import java.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jruby.*;
import org.jruby.javasupport.JavaUtil;
import org.jruby.ext.bigdecimal.RubyBigDecimal;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.SafeDoubleParser;

public class RubyUtils {

    public static RubyObject rubyObject(Ruby ruby, Object node) {
        return (RubyObject) JavaUtil.convertJavaToRuby(ruby, node);
    }

    public static RubyString rubyString(Ruby ruby, String node) {
        return RubyString.newUnicodeString(ruby, node);
    }

    public static RubyString rubyString(Ruby ruby, byte[] node) {
        return RubyString.newString(ruby, node);
    }

    public static RubyString rubyString(Ruby ruby, CharSequence node) {
        return RubyString.newUnicodeString(ruby, node);
    }

    public static RubySymbol rubySymbol(Ruby ruby, String node) {
        return RubySymbol.newSymbol(ruby, node);
    }
   
    public static RubyArray rubyArray(Ruby ruby, Object[] arg) {
        return (RubyArray) JavaUtil.convertJavaToRuby(ruby, arg);
    }

    public static RubyArray rubyArray(Ruby ruby, List arg) {
        return (RubyArray) JavaUtil.convertJavaToRuby(ruby, arg);
    }

    public static RubyHash rubyHash(Ruby ruby, Map arg) {
        return (RubyHash) JavaUtil.convertJavaToRuby(ruby, arg);
    }

    public static RubyFixnum rubyFixnum(Ruby ruby, int arg) {
        return ruby.newFixnum(arg);
    }

    public static RubyFixnum rubyFixnum(Ruby ruby, long arg) {
        return ruby.newFixnum(arg);
    }

    public static RubyBignum rubyBignum(Ruby ruby, BigInteger arg) {
        return RubyBignum.newBignum(ruby, arg);
    }

    public static RubyFloat rubyFloat(Ruby ruby, double arg) {
        return ruby.newFloat(arg);
    }

    public static RubyFloat rubyFloat(Ruby ruby, String arg) {
        double d = SafeDoubleParser.parseDouble(arg);
        return ruby.newFloat(d);
    }

    public static RubyBigDecimal rubyBigDecimal(Ruby ruby, BigDecimal arg) {
        return new RubyBigDecimal(ruby, arg);
    }

    public static RubyBoolean rubyBoolean(Ruby ruby, Boolean arg) {
        return ruby.newBoolean(arg);
    }

    public static RubyHash rubyHash(Ruby ruby, IRubyObject key, IRubyObject value) {
        RubyHash hash = RubyHash.newHash(ruby);
        hash.fastASet(key, value);
        return hash;
    }

    public static RubyHash rubyHash(Ruby ruby, IRubyObject key1, IRubyObject value1,
                                      IRubyObject key2, IRubyObject value2) {
        RubyHash hash = rubyHash(ruby, key1, value1);
        hash.fastASet(key2, value2);
        return hash;
    }
}
