package com.jrjackson;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.SerializationConfig;

//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.text.DateFormat;
import org.jruby.RubyArray;
import org.jruby.RubyBignum;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyFloat;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubyStruct;
import org.jruby.RubySymbol;
import org.jruby.RubyTime;


import org.jruby.ext.bigdecimal.RubyBigDecimal;
import org.jruby.runtime.ThreadContext;
import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.java.proxies.JavaProxy;
import org.jruby.runtime.builtin.IRubyObject;

//public class RubyAnySerializer extends StdSerializer<IRubyObject> {
public class RubyAnySerializer {

    /**
     * Singleton instance to use.""
     */
    public static final RubyAnySerializer instance = new RubyAnySerializer();

    public RubyAnySerializer() {
//        super(IRubyObject.class);
    }

    private void serializeUnknownRubyObject(ThreadContext ctx, IRubyObject rubyObject, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        RubyClass meta = rubyObject.getMetaClass();

        DynamicMethod method = meta.searchMethod("to_time");
        if (!method.isUndefined()) {
            RubyTime dt = (RubyTime) method.call(ctx, rubyObject, meta, "to_time");
//            System.err.println("------->>>> calling to_time");
            serializeTime(dt, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_h");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_h");
            serializeHash(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_hash");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_hash");
            serializeHash(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_a");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_a");
            serializeArray(obj, jgen, provider);
            return;
        }

        method = meta.searchMethod("to_json");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_json");
            if (obj instanceof RubyString) {
                jgen.writeRawValue(obj.toString());
            } else {
                serialize(obj, jgen, provider);
            }
            return;
        }
        throw new JsonGenerationException("Cannot find Serializer for class: " + rubyObject.getClass().getName());
    }

//    @Override
    public void serialize(IRubyObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {

        if (value.isNil()) {

            jgen.writeNull(); // for RubyNil and NullObjects

        } else if (value instanceof JavaProxy) {

            jgen.writeObject(value.dataGetStruct());

//            provider.defaultSerializeValue(value.dataGetStruct(), jgen);

        } else if (value instanceof RubyString) {

            jgen.writeString(value.toString());

        } else if (value instanceof RubySymbol) {

            jgen.writeString(value.toString());

        } else if (value instanceof RubyBoolean) {

            jgen.writeBoolean(value.isTrue());

        } else if (value instanceof RubyFloat) {

            jgen.writeNumber(RubyNumeric.num2dbl(value));

        } else if (value instanceof RubyFixnum) {

            jgen.writeNumber(RubyNumeric.num2long(value));

        } else if (value instanceof RubyBignum) {

            jgen.writeNumber(((RubyBignum) value).getBigIntegerValue());

        } else if (value instanceof RubyBigDecimal) {

            jgen.writeNumber(((RubyBigDecimal) value).getBigDecimalValue());

        } else if (value instanceof RubyHash) {

            serializeHash(value, jgen, provider);

        } else if (value instanceof RubyArray) {

            serializeArray(value, jgen, provider);

        } else if (value instanceof RubyStruct) {

            IRubyObject obj = value.callMethod(value.getRuntime().getCurrentContext(), "to_a");
            serializeArray(obj, jgen, provider);

        } else if (value instanceof RubyTime) {
//            System.err.println("------->>>> RubyTime");
            serializeTime((RubyTime) value, jgen, provider);

        } else {
            serializeUnknownRubyObject(value.getRuntime().getCurrentContext(), value, jgen, provider);
        }
    }

    private void serializeArray(IRubyObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
//            System.err.println("----->> RubyArray");

        RubyArray arr = (RubyArray) value;
        IRubyObject[] a = arr.toJavaArray();
        jgen.writeStartArray();
        for (IRubyObject val : a) {
            serialize(val, jgen, provider);
        }
        jgen.writeEndArray();
    }

    private void serializeHash(IRubyObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
//            System.err.println("----->> RubyHash");

        RubyHash h = (RubyHash) value;
        jgen.writeStartObject();
        for (Object o : h.directEntrySet()) {
            RubyHash.RubyHashEntry next = (RubyHash.RubyHashEntry) o;
            serializeKey((IRubyObject) next.getKey(), jgen, provider);
            serialize((IRubyObject) next.getValue(), jgen, provider);
        }
        jgen.writeEndObject();
    }

    private void serializeTime(RubyTime dt, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
//        DateFormat df = provider.getConfig().getDateFormat();
        SerializationConfig cfg = provider.getConfig();
        DateFormat df = cfg.getDateFormat();
        if (df == null) {
            // DateFormat should always be set
            System.err.println("---------- no format given, Hmmmm");
            provider.defaultSerializeValue(dt.getJavaDate(), jgen);
        } else if (df instanceof RubyDateFormat) {
//            System.err.println("---------- using internal to_s mechanism");
            jgen.writeString(dt.to_s().asJavaString());
        } else {
//            System.err.println("---------- using not simple date format");
            provider.defaultSerializeValue(dt.getJavaDate(), jgen);
        }
    }

    private void serializeKey(IRubyObject key, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {

        jgen.writeFieldName(key.asJavaString());

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
//    @Override
    public void serializeWithType(IRubyObject value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
}
