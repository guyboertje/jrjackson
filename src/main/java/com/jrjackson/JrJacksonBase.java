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
import java.io.ByteArrayOutputStream;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.ext.stringio.StringIO;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.jcodings.specific.UTF8Encoding;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubySymbol;
import org.jruby.util.ByteList;

/**
 *
 * @author guy
 */
public class JrJacksonBase extends RubyObject {

    private static final SimpleDateFormat RDF = new RubyDateFormat("yyyy-MM-dd HH:mm:ss Z");

    @JRubyMethod(module = true, name = {"gen"}, required = 1, optional = 1)
    public static IRubyObject gen(ThreadContext context, IRubyObject self, IRubyObject[] args)
            throws IOException, RaiseException {
        Ruby _ruby = context.runtime;
        RubyHash h = (RubyHash) args[0];

//        %Q|{"message"=>"#{o['message']}", "@version"=>"1", "@timestamp"=>"#{ts}", "host"=>"#{o['host']}"}|
//        RubyString s = RubyString.newString(_ruby, "{\"message\"=>\"");
//        s.cat(str)
        String out = "{\"message\"=>\""
                + h.get("message").toString()
                + "\", \"@version\"=>\"1\", \"@timestamp\"=>\""
                + h.get("@timestamp").toString()
                + "\", \"host\"=>\""
                + h.get("host").toString()
                + "\"}";
        ByteList bl = new ByteList(out.getBytes(StandardCharsets.UTF_8),
                    UTF8Encoding.INSTANCE);
        return RubyString.newString(_ruby, bl);
    }

    // serialize
    @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1, optional = 1)
    public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject[] args)
            throws IOException, RaiseException {
        Ruby _ruby = context.runtime;
        RubyHash options = (args.length <= 1) ? RubyHash.newHash(_ruby) : args[1].convertToHash();
        String format = (String) options.get(RubyUtils.rubySymbol(_ruby, "date_format"));

//        StringWriter out = new StringWriter();
//        JsonGenerator jgen = RubyJacksonModule.factory.createGenerator(out);

        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator jgen = RubyJacksonModule.factory.createGenerator(
                baos, JsonEncoding.UTF8);
        
        
        
        SimpleDateFormat simpleFormat;
        if (format != null) {
            simpleFormat = new SimpleDateFormat(format);
            String timezone = (String) options.get(RubyUtils.rubySymbol(_ruby, "timezone"));
            if (timezone != null) {
                simpleFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            }
        } else {
            // using a 'marker' class instance, will not use later but default to #to_s
            simpleFormat = RDF;
        }

        try {
            RubyAnySerializer.instance.serialize(args[0], jgen,
                    RubyJacksonModule.createProvider(simpleFormat));
            jgen.close();
            ByteList bl = new ByteList(baos.toByteArray(),
                    UTF8Encoding.INSTANCE);
            return RubyString.newString(_ruby, bl);
//            return RubyUtils.rubyString(_ruby, out.toString());
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
