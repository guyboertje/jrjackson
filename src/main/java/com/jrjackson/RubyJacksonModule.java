package com.jrjackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import java.text.SimpleDateFormat;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyJacksonModule extends SimpleModule {

    private static final SimpleDateFormat RDF = new RubyDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private static final ObjectMapper static_mapper = new ObjectMapper();
    public static final JsonFactory factory = new JsonFactory(static_mapper).disable(JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW);

    static {
        static_mapper.registerModule(new RubyJacksonModule().addSerializer(
                IRubyObject.class, RubyAnySerializer.instance
        ));
        static_mapper.registerModule(new AfterburnerModule());
        static_mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private RubyJacksonModule() {
        super("JrJacksonStrModule", new Version(1, 2, 18, "0", "com.jrjackson.jruby", "jrjackson"));
    }

    public static ObjectMapper mapperWith(Ruby ruby, RubyKeyConverter nameConverter,
            RubyConverter intConverter, RubyConverter floatConverter) {

        return static_mapper.copy().registerModule(
                new RubyJacksonModule().addDeserializer(
                        Object.class,
                        new RubyObjectDeserializer().with(ruby, nameConverter, intConverter, floatConverter)
                )
        );
    }

    public static ObjectMapper rawMapper() {
        return static_mapper;
    }

    public static DefaultSerializerProvider createProvider(SimpleDateFormat sdf) {
        static_mapper.setDateFormat(sdf);
        return ((DefaultSerializerProvider) static_mapper.getSerializerProvider()).createInstance(
                static_mapper.getSerializationConfig(),
                static_mapper.getSerializerFactory()
        );
    }

    public static DefaultSerializerProvider createProvider() {
        static_mapper.setDateFormat(RDF);
        return ((DefaultSerializerProvider) static_mapper.getSerializerProvider()).createInstance(
                static_mapper.getSerializationConfig(),
                static_mapper.getSerializerFactory()
        );
    }

    public static ObjectMapper rawBigNumberMapper() {
        static_mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        static_mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        return static_mapper;
    }

}
