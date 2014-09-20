package com.jrjackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import org.jruby.*;
import org.jruby.runtime.ThreadContext;
import org.jruby.internal.runtime.methods.DynamicMethod;

public class RubyAnySerializer extends StdSerializer<RubyObject> {

    /**
     * Singleton instance to use.
     */
    public static final RubyAnySerializer instance = new RubyAnySerializer();
    private static final HashMap<Class, Class> class_maps = new HashMap<Class, Class>();

    static {
        class_maps.put(RubyBoolean.class, Boolean.class);
    }

    public RubyAnySerializer() {
        super(RubyObject.class);
    }

    private Class<?> rubyJavaClassLookup(Class target) {
        Class<?> val = class_maps.get(target);
        if (val == null) {
            return Object.class;
        }
        return val;
    }

    private void serializeUnknownRubyObject(ThreadContext ctx, RubyObject rubyObject, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        RubyClass meta = rubyObject.getMetaClass();

        DynamicMethod method = meta.searchMethod("to_time");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_time");
            provider.defaultSerializeValue(obj, jgen);
            return;
        }

        method = meta.searchMethod("to_h");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_h");
            provider.findTypedValueSerializer(Map.class, true, null).serialize(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_hash");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_hash");
            provider.findTypedValueSerializer(Map.class, true, null).serialize(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_a");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_a");
            provider.findTypedValueSerializer(List.class, true, null).serialize(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_json");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_json");
            if (obj instanceof RubyString) {
                jgen.writeRawValue(obj.toString());
            } else {
                provider.defaultSerializeValue(obj, jgen);
            }
            return;
        }
        throw new JsonGenerationException("Cannot find Serializer for class: " + rubyObject.getClass().getName());
    }

    @Override
    public void serialize(RubyObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        ThreadContext ctx = value.getRuntime().getCurrentContext();
        if (value instanceof RubySymbol || value instanceof RubyString) {
            jgen.writeString(value.toString());
        } else if (value instanceof RubyHash) {
            provider.findTypedValueSerializer(Map.class, true, null).serialize(value, jgen, provider);
        } else if (value instanceof RubyArray) {
            provider.findTypedValueSerializer(List.class, true, null).serialize(value, jgen, provider);
        } else if (value instanceof RubyStruct) {
            RubyObject obj = (RubyObject)value.callMethod(ctx, "to_a");
            provider.findTypedValueSerializer(List.class, true, null).serialize(obj, jgen, provider);
        } else {
            Object val = value.toJava(rubyJavaClassLookup(value.getClass()));
            if (val instanceof RubyObject) {
                serializeUnknownRubyObject(ctx, (RubyObject) val, jgen, provider);
            } else {
                provider.defaultSerializeValue(val, jgen);
            }
        }
    }

    /**
     * Default implementation will write type prefix, call regular serialization method (since assumption is that value itself does not need JSON Array or Object start/end markers), and then write type suffix. This should work for most cases; some sub-classes may want to change this behavior.
     *
     * @param value
     * @param jgen
     * @param provider
     * @param typeSer
     * @throws java.io.IOException
     * @throws com.fasterxml.jackson.core.JsonGenerationException
     */
    @Override
    public void serializeWithType(RubyObject value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
}
