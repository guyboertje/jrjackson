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


@JRubyModule(name = "JrJacksonJava")
public class JrJacksonJava extends JrJacksonBase {

    public JrJacksonJava(Ruby ruby, RubyClass metaclass) {
        super(ruby, metaclass);
    }

    // deserialize
    @JRubyMethod(module = true, name = {"parse", "load"}, required = 2)
    public static IRubyObject parse(ThreadContext context, IRubyObject self, IRubyObject arg, IRubyObject opts)
            throws JsonProcessingException, IOException, RaiseException {
        
        JavaHandler handler = new JavaHandler();
        JjParse parse = new JjParse(handler);
        ObjectMapper mapper = RubyJacksonModule.rawBigNumberMapper();
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
}
