package com.jrjackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.anno.JRubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jruby.exceptions.RaiseException;


@JRubyModule(name = "JrJacksonRuby")
public class JrJacksonRuby extends JrJacksonBase {

    public JrJacksonRuby(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    // deserialize
     @JRubyMethod(module = true, name = {"parse_sym", "load_sym"}, required = 2)
    public static Object parse_sym(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws JsonProcessingException, IOException, RaiseException {
        
        return _parse(context, arg, new RubySymbolNameConverter());
    }
    
    @JRubyMethod(module = true, name = {"parse", "load"}, required = 2)
    public static Object parse(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws JsonProcessingException, IOException, RaiseException {
        
        return _parse(context, arg, new RubyStringNameConverter());
    }
    
    private static Object _parse(ThreadContext context, IRubyObject arg, RubyNameConverter konv) 
            throws JsonProcessingException, IOException, RaiseException {
        
        RubyHandler handler = new RubyHandler(context,
                konv,
                new RubyBigIntValueConverter(),
                new RubyBigDecimalValueConverter());
        JrParse parse = new JrParse(handler);
        ObjectMapper mapper = RubyJacksonModule.rawBigNumberMapper();
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
}
