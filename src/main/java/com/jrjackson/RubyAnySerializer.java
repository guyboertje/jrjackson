package com.jrjackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.jruby.ext.bigdecimal.RubyBigDecimal;

import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.java.proxies.JavaProxy;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.jruby.RubyArray;
import org.jruby.RubyBignum;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyNumeric;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.RubyTime;


public class RubyAnySerializer extends JsonSerializer<IRubyObject> {

    enum RUBYCLASS {

        String,
        Float,
        BigDecimal,
        Time,
        Array,
        Hash,
        Fixnum,
        Bignum,
        Date,
        Symbol,
        Struct,
        TrueClass,
        FalseClass;
    }

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

        DynamicMethod method = meta.searchMethod("to_json_data");
        if (!method.isUndefined()) {
            RubyObject obj = (RubyObject) method.call(ctx, rubyObject, meta, "to_json_data");
            if (obj instanceof RubyString) {
                RubyUtils.writeBytes(obj, jgen);
            } else {
                serialize(obj, jgen, provider);
            }
            return;
        }

        method = meta.searchMethod("to_time");
        if (!method.isUndefined()) {
            RubyTime dt = (RubyTime) method.call(ctx, rubyObject, meta, "to_time");
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
        throw new JsonGenerationException("Cannot serialize instance of: " + meta.getRealClass().getName(), jgen);
    }

    @Override
    public void serialize(IRubyObject value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {

        if (value.isNil()) {

            jgen.writeNull(); // for RubyNil and NullObjects

        } else if (value instanceof JavaProxy) {

            provider.defaultSerializeValue(((JavaProxy) value).getObject(), jgen);

        } else {

            String rubyClassName = value.getMetaClass().getRealClass().getName();
            RUBYCLASS clazz;

            try {
                clazz = RUBYCLASS.valueOf(rubyClassName);
            } catch (IllegalArgumentException e) {
                serializeUnknownRubyObject(value.getRuntime().getCurrentContext(), value, jgen, provider);
                return;
            }

            switch (clazz) {
                case Hash:
                    serializeHash(value, jgen, provider);
                    break;
                case Array:
                    serializeArray(value, jgen, provider);
                    break;
                case String:
                    RubyUtils.writeBytes(value, jgen);
                    break;
                case Symbol:
                case Date:
                    // Date to_s -> yyyy-mm-dd
                    RubyString s = value.asString();
                    jgen.writeUTF8String(s.getBytes(), 0, s.size());
                    break;
                case TrueClass:
                case FalseClass:
                    jgen.writeBoolean(value.isTrue());
                    break;
                case Float:
                    jgen.writeNumber(RubyNumeric.num2dbl(value));
                    break;
                case Fixnum:
                    jgen.writeNumber(RubyNumeric.num2long(value));
                    break;
                case Bignum:
                    jgen.writeNumber(((RubyBignum) value).getBigIntegerValue());
                    break;
                case BigDecimal:
                    jgen.writeNumber(((RubyBigDecimal) value).getBigDecimalValue());
                    break;
                case Struct:
                    IRubyObject obj = value.callMethod(value.getRuntime().getCurrentContext(), "to_a");
                    serializeArray(obj, jgen, provider);
                    break;
                case Time:
                    serializeTime((RubyTime) value, jgen, provider);
                    break;
                default:
                    serializeUnknownRubyObject(value.getRuntime().getCurrentContext(), value, jgen, provider);
                    break;
            }
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
        DateFormat df = provider.getConfig().getDateFormat();
        if (df == null) {
            // DateFormat should always be set
            provider.defaultSerializeDateValue(dt.getJavaDate(), jgen);
        } else if (df instanceof RubyDateFormat) {
            // why another branch? I thought there was an easy win on to_s
            // maybe with jruby 9000
            RubyDateFormat rdf = (RubyDateFormat) df.clone();
            jgen.writeString(rdf.format(dt.getJavaDate()));
        } else {
            SimpleDateFormat sdf = (SimpleDateFormat) df.clone();
            jgen.writeString(df.format(dt.getJavaDate()));
        }
    }

    private void serializeKey(IRubyObject key, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        if (key instanceof RubyString) {
            jgen.writeFieldName(((RubyString) key).decodeString());
        } else {
            // includes RubySymbol and non RubyString objects
            jgen.writeFieldName(key.toString());
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
    public void serializeWithType(IRubyObject value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
}
