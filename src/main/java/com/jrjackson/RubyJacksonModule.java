package com.jrjackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import org.jruby.Ruby;
import org.jruby.RubyObject;

public class RubyJacksonModule extends SimpleModule {

    private static final ObjectMapper static_mapper = new ObjectMapper().registerModule(
            new RubyJacksonModule().addSerializer(RubyObject.class, RubyAnySerializer.instance)
    );

    static {
        static_mapper.registerModule(new AfterburnerModule());
        static_mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private RubyJacksonModule() {
        super("JrJacksonStrModule", VersionUtil.versionFor(RubyJacksonModule.class));
    }
    
    public static ObjectMapper mapperWith(Ruby ruby, RubyKeyConverter nameConverter,
            RubyValueConverter intConverter, RubyValueConverter floatConverter){
        
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new AfterburnerModule()
        );
        
        mapper.registerModule(
            new RubyJacksonModule().addSerializer(
                RubyObject.class, RubyAnySerializer.instance
            ).addDeserializer(
                Object.class,
                new RubyObjectDeserializer().with(ruby, nameConverter, intConverter, floatConverter)
            )
        );
        
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static ObjectMapper rawMapper() {
        return static_mapper;
    }
    
    public static ObjectMapper rawBigDecimalMapper() {
//        ObjectMapper mapper = new ObjectMapper().registerModule(
//                new AfterburnerModule()
//        ).registerModule(
//                new RubyJacksonModule().addSerializer(
//                    RubyObject.class, RubyAnySerializer.instance
//                )
//        );
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        static_mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return static_mapper;
    }
    
    public static ObjectMapper symMapper(Ruby ruby) {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new AfterburnerModule()
        );

        mapper.registerModule(
                new RubyJacksonModule().addSerializer(
                        RubyObject.class, RubyAnySerializer.instance
                ).addDeserializer(
                        Object.class,
                        new RubyObjectDeserializer().with(
                            ruby,
                            new RubySymbolNameConverter(),
                            new RubyBigIntValueConverter(),
                            new RubyFloatValueConverter()
                        )
                )
        );
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        return mapper;
    }
    
    public static ObjectMapper strMapper(Ruby ruby) {
        ObjectMapper mapper = new ObjectMapper().registerModule(
                new AfterburnerModule()
        );

        mapper.registerModule(
                new RubyJacksonModule().addSerializer(
                        RubyObject.class, RubyAnySerializer.instance
                ).addDeserializer(
                        Object.class,
                        new RubyObjectDeserializer().with(
                            ruby,
                            new RubyStringNameConverter(),
                            new RubyBigIntValueConverter(),
                            new RubyFloatValueConverter()
                        )
                )
        );
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        return mapper;
    }
}
