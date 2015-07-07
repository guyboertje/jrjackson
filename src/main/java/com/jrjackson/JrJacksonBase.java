/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jrjackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.exceptions.RaiseException;
import org.jruby.ext.stringio.StringIO;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.anno.JRubyMethod;

/**
 *
 * @author guy
 */
public class JrJacksonBase extends RubyObject {

    // serialize
    @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1, optional = 1)
    public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject[] args) throws IOException, RaiseException {
        Ruby _ruby = context.runtime;
        Object obj = args[0].toJava(Object.class);
        RubyHash options = (args.length <= 1) ? RubyHash.newHash(_ruby) : args[1].convertToHash();
        String format = (String) options.get(RubyUtils.rubySymbol(_ruby, "date_format"));
        ObjectMapper mapper = RubyJacksonModule.rawMapper();
        if (format != null) {
            SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
            String timezone = (String) options.get(RubyUtils.rubySymbol(_ruby, "timezone"));
            if (timezone != null) {
                simpleFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            }
            mapper.setDateFormat(simpleFormat);
        } else {
            // using a 'marker' class instance, will not use later but default to #to_s
            mapper.setDateFormat(new RubyDateFormat("yyyy-MM-dd HH:mm:ss Z"));
        }
        try {
            String s = mapper.writeValueAsString(obj);
            return RubyUtils.rubyString(_ruby, s);
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

    protected static IRubyObject _sjcparse(ThreadContext context, IRubyObject handler, IRubyObject arg, IRubyObject opts, StreamParse sp) throws RaiseException {
        ObjectMapper mapper = RubyJacksonModule.rawMapper();
        JsonFactory jf = mapper.getFactory();
        JsonParser jp;
        try {
            jp = buildParser(context, jf, arg);
        } catch (IOException e) {
            throw context.runtime.newIOError(e.getLocalizedMessage());
        }
        return sp.deserialize(jp);
    }

    protected static JsonParser buildParser(ThreadContext ctx, JsonFactory jf, IRubyObject arg) throws IOException {
        if (arg instanceof RubyString) {
            return jf.createParser(((RubyString) arg).getByteList().bytes());
        } else if (arg instanceof StringIO) {
            RubyString content = (RubyString) ((StringIO) arg).string(ctx);
            return jf.createParser(content.getByteList().bytes());
        } else {
            // must be an IO object then
            return jf.createParser(((RubyIO) arg).getInStream());
        }
    }

    protected static IRubyObject _parse(ThreadContext context, IRubyObject arg, ObjectMapper mapper) throws IOException, RaiseException {
        Ruby ruby = context.runtime;
        // same format as Ruby Time #to_s
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        mapper.setDateFormat(simpleFormat);
        try {
            Object o;
            if (arg instanceof RubyString) {
                o = mapper.readValue(((RubyString) arg).getByteList().bytes(), Object.class);
            } else if (arg instanceof StringIO) {
                RubyString content = (RubyString) ((StringIO) arg).string(context);
                o = mapper.readValue(content.getByteList().bytes(), Object.class);
            } else {
                // must be an IO object then
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
