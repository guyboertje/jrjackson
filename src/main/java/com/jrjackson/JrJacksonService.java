package com.jrjackson;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.BasicLibraryService;

import java.io.IOException;

public class JrJacksonService implements BasicLibraryService {
  public boolean basicLoad(final Ruby ruby) throws IOException {
    RubyModule jr_jackson = ruby.defineModule("JrJackson");

    RubyModule jr_jackson_raw = ruby.defineModuleUnder("Raw", jr_jackson);
    jr_jackson_raw.defineAnnotatedMethods(JrJacksonRaw.class);

    RubyClass runtimeError = ruby.getRuntimeError();
    RubyClass parseError = jr_jackson.defineClassUnder("ParseError", runtimeError, runtimeError.getAllocator());
    return true;
  }
}
