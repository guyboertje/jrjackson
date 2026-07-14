/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jrjackson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jcodings.specific.UTF8Encoding;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author guy
 */
public class JrJacksonBase extends RubyObject {

    // serialize
    @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1, optional = 1)
    public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject[] args)
            throws IOException, RaiseException {
        Ruby _ruby = context.runtime;
        RubyHash options = (args.length <= 1) ? RubyHash.newHash(_ruby) : args[1].convertToHash();
        String format = (String) options.get(RubyUtils.rubySymbol(_ruby, "date_format"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator jgen = RubyJacksonModule.factory.createGenerator(
                baos, JsonEncoding.UTF8);

        if (flagged(options, RubyUtils.rubySymbol(_ruby, "pretty"))) {
            jgen.useDefaultPrettyPrinter();
        }

        Boolean stringifyBigDecimal = flagged(options, RubyUtils.rubySymbol(_ruby, "stringify_bigdecimal"));
        SerializerProvider provider;
        if (format != null) {
            SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
            String timezone = (String) options.get(RubyUtils.rubySymbol(_ruby, "timezone"));
            if (timezone != null) {
                simpleFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            }
            provider = RubyJacksonModule.createProvider(simpleFormat);
        } else {
            provider = RubyJacksonModule.createProvider();
        }

        try {
            new RubyAnySerializer(stringifyBigDecimal).serialize(args[0], jgen, provider);
            jgen.close();
            ByteList bl = new ByteList(baos.toByteArray(),
                    UTF8Encoding.INSTANCE);
            return RubyString.newString(_ruby, bl);
        } catch (JsonProcessingException e) {
            throw ParseError.newParseError(_ruby, e.getLocalizedMessage());
        }
    }

    protected static boolean flagged(RubyHash opts, RubySymbol key) {
        Object val = opts.get(key);
        if (val == null) {
            return false;
        }
        boolean flag = (Boolean) val;
        return flag;
    }

    protected static boolean flagged(RubyHash opts, RubySymbol key, boolean returnVal) {
        if (!opts.containsKey(key)) {
            return returnVal;
        }
        Object val = opts.get(key);
        if (val == null) {
            return returnVal;
        }
        boolean flag = (Boolean) val;
        return flag;
    }

    protected static IRubyObject _sjcparse(ThreadContext context, IRubyObject handler, IRubyObject arg, IRubyObject opts, StreamParse sp) throws RaiseException {

        JsonParser jp;
        try {
            jp = buildParser(context, RubyJacksonModule.factory, arg);
        } catch (IOException e) {
            throw context.runtime.newIOError(e.getLocalizedMessage());
        }
        return sp.deserialize(jp);
    }

    private static boolean isStringIO(IRubyObject arg) {
        return "StringIO".equals(arg.getMetaClass().getName());
    }

    protected static byte[] extractBytes(ThreadContext ctx, IRubyObject arg) {
        if (arg instanceof RubyString) {
            return ((RubyString) arg).getByteList().bytes();
        } else if (isStringIO(arg)) {
            RubyString content = arg.callMethod(ctx, "string").convertToString();
            return content.getByteList().bytes();
        }
        return null;
    }

    protected static JsonParser buildParser(ThreadContext ctx, JsonFactory jf, IRubyObject arg) throws IOException {
        byte[] bytes = extractBytes(ctx, arg);
        if (bytes != null) {
            return jf.createParser(bytes);
        }
        return jf.createParser(((RubyIO) arg).getInStream());
    }

    protected static IRubyObject _parse(ThreadContext context, IRubyObject arg, ObjectMapper mapper) throws IOException, RaiseException {
        Ruby ruby = context.runtime;
        // same format as Ruby Time #to_s
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        mapper.setDateFormat(simpleFormat);
        try {
            Object o;
            byte[] bytes = extractBytes(context, arg);
            if (bytes != null) {
                o = mapper.readValue(bytes, Object.class);
            } else {
                o = mapper.readValue(((RubyIO) arg).getInStream(), Object.class);
            }
            return RubyUtils.rubyObject(ruby, o);
        } catch (JsonProcessingException e) {
            throw ParseError.newParseError(ruby, e.getLocalizedMessage());
        } catch (IOException e) {
            throw ruby.newIOError(e.getLocalizedMessage());
        }
    }

    public JrJacksonBase(Ruby runtime, RubyClass metaClass) {
        super(runtime, metaClass);
    }

}
