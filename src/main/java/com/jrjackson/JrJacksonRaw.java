package com.jrjackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyModule;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.ext.stringio.StringIO;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jruby.exceptions.RaiseException;


@JRubyModule(name = "JrJacksonRaw")
public class JrJacksonRaw extends RubyObject {

    public JrJacksonRaw(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    private static boolean flagged(RubyHash opts, RubySymbol key) {
        Object val = opts.get(key);
        if (val == null) {
            return false;
        }
        return (Boolean) val;
    }

    // deserialize
    @JRubyMethod(module = true, name = {"parse", "load"}, required = 2)
    public static IRubyObject parse(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws IOException {
        RubyHash options = null;
        ObjectMapper local = null;
        Ruby _ruby = context.runtime;
        
        boolean use_big_decimal = false;
        boolean use_big_int     = true;

        if (opts != context.nil) {
            options = opts.convertToHash();
            
            if (flagged(options, RubyUtils.rubySymbol(_ruby, "use_bigdecimal"))) {
                use_big_decimal = true;
            }
            
            if (flagged(options, RubyUtils.rubySymbol(_ruby, "raw"))) {
                local = RubyJacksonModule.rawMapper();
                if (use_big_decimal) {
                    local.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
                } else {
                    local.disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
                }
                return _parse(context, arg, local);
            }
            
            RubyConverter vci = new RubyBigIntValueConverter();
            RubyConverter vcf = new RubyFloatValueConverter();
            RubyKeyConverter   kcn = new RubyStringKeyConverter();

            if (flagged(options, RubyUtils.rubySymbol(_ruby, "symbolize_keys"))) {
                kcn = new RubySymbolKeyConverter();
            }

            if (flagged(options, RubyUtils.rubySymbol(_ruby, "use_smallint"))) {
                use_big_int = false;
                vci = new RubyIntValueConverter();
            }

            if (use_big_decimal) {
                vcf = new RubyBigDecimalValueConverter();
            }
            
            local = RubyJacksonModule.mapperWith(_ruby, kcn, vci, vcf);

        } else {
            local = RubyJacksonModule.strMapper(_ruby);
        }

        if (use_big_decimal) {
           local.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        } else {
           local.disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); 
        }
        
        if (use_big_int) {
            local.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        } else {
            local.disable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        }
        
        return _parse(context, arg, local);
    }
    
    @JRubyMethod(module = true, name = {"parse_raw", "load_raw"}, required = 1)
    public static IRubyObject parse_raw(ThreadContext context, IRubyObject self, IRubyObject arg)
            throws IOException, RaiseException {
        ObjectMapper mapper = RubyJacksonModule.rawMapper();
        return _parse(context, arg, mapper);
    }
    
    @JRubyMethod(module = true, name = {"parse_raw_bd", "load_raw_bd"}, required = 1)
    public static IRubyObject parse_raw_bd(ThreadContext context, IRubyObject self, IRubyObject arg)
            throws IOException, RaiseException {
        ObjectMapper mapper = RubyJacksonModule.rawBigDecimalMapper();
        return _parse(context, arg, mapper);
    }

    @JRubyMethod(module = true, name = {"parse_sym", "load_sym"}, required = 1)
    public static IRubyObject parse_sym(ThreadContext context, IRubyObject self, IRubyObject arg)
            throws IOException, RaiseException {
        ObjectMapper mapper = RubyJacksonModule.symMapper(context.runtime);
        return _parse(context, arg, mapper);
    }

    @JRubyMethod(module = true, name = {"parse_str", "load_str"}, required = 1)
    public static IRubyObject parse_str(ThreadContext context, IRubyObject self, IRubyObject arg)
            throws IOException, RaiseException {
        ObjectMapper mapper = RubyJacksonModule.strMapper(context.runtime);
        return _parse(context, arg, mapper);
    }
    
    @JRubyMethod(module = true, name = {"parse_ro", "load_ro"}, required = 2)
    public static Object parse_ro(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws JsonProcessingException, IOException, RaiseException {
        
        RubyHandler handler = new RubyHandler(context,
                new RubySymbolNameConverter(),
                new RubyBigIntValueConverter(),
                new RubyBigDecimalValueConverter());
        JsParse parse = new JsParse(handler);
        ObjectMapper mapper = RubyJacksonModule.rawBigDecimalMapper();
        JsonFactory jf = mapper.getFactory();
        JsonParser jp;
        try {
            jp = buildParser(context, jf, arg);
        } catch (IOException e) {
            throw context.runtime.newIOError(e.getLocalizedMessage());
        }
        
        parse.deserialize(jp);
        return handler.getResult();
    }
    
    @JRubyMethod(module = true, name = {"parse_jo", "load_jo"}, required = 2)
    public static IRubyObject parse_jo(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws JsonProcessingException, IOException, RaiseException {
        
        JavaHandler handler = new JavaHandler();
        JjParse parse = new JjParse(handler);
        ObjectMapper mapper = RubyJacksonModule.rawBigDecimalMapper();
        JsonFactory jf = mapper.getFactory();
        JsonParser jp;
        try {
            jp = buildParser(context, jf, arg);
        } catch (IOException e) {
            throw context.runtime.newIOError(e.getLocalizedMessage());
        }
        
        parse.deserialize(jp);
        return RubyUtils.rubyObject(context.runtime, handler.getResult());
    }

    @JRubyMethod(module = true, name = {"sj_parse", "sj_load"}, required = 3)
    public static IRubyObject sj_parse(ThreadContext context, IRubyObject self, IRubyObject handler, IRubyObject arg, IRubyObject opts)
            throws RaiseException {
        StreamParse sp = new SajParse(context, handler);
        return _sjcparse(context, handler, arg, opts, sp);
    }
    
    @JRubyMethod(module = true, name = {"sc_parse", "sc_load"}, required = 3)
    public static IRubyObject sc_parse(ThreadContext context, IRubyObject self, IRubyObject handler, IRubyObject arg, IRubyObject opts)
            throws RaiseException {
        StreamParse sp = new SchParse(context, handler);
        return _sjcparse(context, handler, arg, opts, sp);
    }
    
    private static IRubyObject _sjcparse(ThreadContext context, IRubyObject handler, IRubyObject arg, IRubyObject opts, StreamParse sp)
            throws RaiseException {
        
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
    
    private static JsonParser buildParser(ThreadContext ctx, JsonFactory jf, IRubyObject arg) throws IOException {
        if (arg instanceof RubyString) {
            return jf.createParser(
                    ((RubyString) arg).getByteList().bytes()
            );
        } else if (arg instanceof StringIO) {
            RubyString content = (RubyString) ((StringIO) arg).string(ctx);
            return jf.createParser(
                    content.getByteList().bytes()
            );
        } else {
            // must be an IO object then
            return jf.createParser(
                    ((RubyIO) arg).getInStream()
            );
        }
    }

    private static IRubyObject _parse(ThreadContext context, IRubyObject arg, ObjectMapper mapper)
            throws IOException, RaiseException {
        Ruby ruby = context.runtime;
        // same format as Ruby Time #to_s
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        mapper.setDateFormat(simpleFormat);
        try {
            Object o;
            if (arg instanceof RubyString) {
                o = mapper.readValue(
                  ((RubyString) arg).getByteList().bytes(), Object.class
                );
            } else if (arg instanceof StringIO) {
                RubyString content = (RubyString)((StringIO) arg).string(context);
                o = mapper.readValue(
                  content.getByteList().bytes(), Object.class
                );
            } else {
                // must be an IO object then
                o = mapper.readValue(((RubyIO)arg).getInStream(), Object.class);
            }
            return RubyUtils.rubyObject(ruby, o);
        } catch (JsonProcessingException e) {
            throw ParseError.newParseError(ruby, e.getLocalizedMessage());
        } catch (IOException e) {
            throw ruby.newIOError(e.getLocalizedMessage());
        }
    }

    // serialize
    @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1, optional = 1)
    public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject[] args)
            throws IOException, RaiseException {
        Ruby _ruby = context.runtime;
        Object obj = args[0].toJava(Object.class);
        RubyHash options = (args.length <= 1) ? RubyHash.newHash(_ruby) : args[1].convertToHash();
        String format = (String) options.get(RubyUtils.rubySymbol(_ruby, "date_format"));
        
        ObjectMapper mapper = RubyJacksonModule.rawMapper();
        // same format as Ruby Time #to_s
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        
        if (format != null) {
            simpleFormat = new SimpleDateFormat(format);
            String timezone = (String) options.get(RubyUtils.rubySymbol(_ruby, "timezone"));
            if (timezone != null) {
                simpleFormat.setTimeZone(TimeZone.getTimeZone(timezone));
            }
        }
        mapper.setDateFormat(simpleFormat);
        
        try {
            String s = mapper.writeValueAsString(obj);
            return RubyUtils.rubyString(_ruby, s);
        } catch (JsonProcessingException e) {
            throw ParseError.newParseError(_ruby, e.getLocalizedMessage());
        } catch (IOException e) {
            throw _ruby.newIOError(e.getLocalizedMessage());
        }
    }
}
