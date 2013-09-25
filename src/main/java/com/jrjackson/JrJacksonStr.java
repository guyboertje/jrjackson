package com.jrjackson;

// import org.jruby.Ruby;
// import org.jruby.RubyClass;
import org.jruby.RubyObject;
// import org.jruby.RubyString;
// import org.jruby.RubySymbol;
// import org.jruby.RubyIO;
// import org.jruby.anno.JRubyMethod;
// import org.jruby.anno.JRubyModule;
// import org.jruby.exceptions.RaiseException;
// import org.jruby.ext.stringio.RubyStringIO;
// import org.jruby.java.addons.IOJavaAddons;
// import org.jruby.javasupport.JavaUtil;
// import org.jruby.runtime.ThreadContext;
// import org.jruby.runtime.builtin.IRubyObject;
// import org.jruby.util.RubyDateFormat;

// import java.io.InputStream;
// import java.io.IOException;
// import java.text.DateFormat;
// import java.util.*;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.module.SimpleModule;
// import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
// import com.fasterxml.jackson.core.Version;
// import com.fasterxml.jackson.core.JsonProcessingException;

// @JRubyModule(name = "JrJacksonStr")
public class JrJacksonStr extends RubyObject {
//   private static final ObjectMapper mapper = new ObjectMapper();
//   private static final SimpleModule module = new SimpleModule("JrJacksonStrModule");

//   static {
//     module.addDeserializer(Object.class, RubyObjectDeserializer.instance);
//     module.addSerializer(RubySymbol.class, ToStringSerializer.instance);
//     mapper.registerModule(module);
//     mapper.setDateFormat(new RubyDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US, true));
//   }
  
//   public JrJacksonStr(Ruby ruby, RubyClass metaclass) {
//     super(ruby, metaclass);
//   }

//   @JRubyMethod(module = true, name = {"parse", "load"}, required = 1)
//   public static IRubyObject parse(ThreadContext context, IRubyObject self, IRubyObject arg) {
//     Ruby ruby = context.getRuntime();
//     try {
//       Object o;
//       if (arg instanceof RubyString) {
//         o = mapper.readValue(arg.toString(), Object.class);
//       } else if ((arg instanceof RubyIO) || (arg instanceof RubyStringIO)) {
//         IRubyObject stream = IOJavaAddons.AnyIO.any_to_inputstream(context, arg);
//         o =  mapper.readValue((InputStream)stream.toJava(InputStream.class), Object.class);
//       } else {
//         throw ruby.newArgumentError("Unsupported source. This method accepts String or IO");
//       }
//       return (RubyObject)JavaUtil.convertJavaToRuby(ruby, o);
//     }
//     catch (JsonProcessingException e) {
//       throw ParseError.newParseError(ruby, e.getLocalizedMessage());
//     }
//     catch (IOException e) {
//       throw ruby.newIOError(e.getLocalizedMessage());
//     }
//   }

//   @JRubyMethod(module = true, name = {"generate", "dump"}, required = 1)
//   public static IRubyObject generate(ThreadContext context, IRubyObject self, IRubyObject arg) {
//     Ruby ruby = context.getRuntime();
//     Object obj = arg.toJava(Object.class);
//     try {
//       String s = mapper.writeValueAsString(obj);
//       return ruby.newString(s);
//     }
//     catch (JsonProcessingException e) {
//       throw ParseError.newParseError(ruby, e.getLocalizedMessage());
//     }
//     catch (IOException e) {
//       throw ruby.newIOError(e.getLocalizedMessage());
//     }
//   }
}
