package com.jrjackson;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyString;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.javasupport.util.RuntimeHelpers;


public abstract class RubyObjectDeserializer
    extends StdDeserializer<RubyObject>
{
    private static final long serialVersionUID = 1L;

    private final static RubyObject[] NO_OBJECTS = new RubyObject[0];

    protected final static Ruby __ruby__ = Ruby.getGlobalRuntime();

    /**
     * @since 2.2
     */
    
    public RubyObjectDeserializer() { super(RubyObject.class); }

    protected abstract RubyObject convertKey(JsonParser jp)
      throws IOException;

    /*
    /**********************************************************
    /* Deserializer API
    /**********************************************************
     */
    
    @Override
    public RubyObject deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        switch (jp.getCurrentToken()) {
        case START_OBJECT:
            return mapObject(jp, ctxt);
        case START_ARRAY:
            return mapArray(jp, ctxt);
        case FIELD_NAME:
            return mapObject(jp, ctxt);
        case VALUE_EMBEDDED_OBJECT:
            return toRuby(jp.getEmbeddedObject());
        case VALUE_STRING:
            return rubyString(jp);

        case VALUE_NUMBER_INT:
            /* [JACKSON-100]: caller may want to get all integral values
             * returned as BigInteger, for consistency
             */
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                return toRuby(jp.getBigIntegerValue()); // should be optimal, whatever it is
            }
            return toRuby(jp.getNumberValue()); // should be optimal, whatever it is

        case VALUE_NUMBER_FLOAT:
            /* [JACKSON-72]: need to allow overriding the behavior regarding
             *   which type to use
             */
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                return toRuby(jp.getDecimalValue());
            }
            return toRuby(Double.valueOf(jp.getDoubleValue()));

        case VALUE_TRUE:
            return __ruby__.newBoolean(Boolean.TRUE);
        case VALUE_FALSE:
            return __ruby__.newBoolean(Boolean.FALSE);

        case VALUE_NULL: // should not get this but...
            return null;

        case END_ARRAY: // invalid
        case END_OBJECT: // invalid
        default:
            throw ctxt.mappingException(Object.class);
        }
    }

    @Override
    public RubyObject deserializeWithType(JsonParser jp, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        switch (t) {
        // First: does it look like we had type id wrapping of some kind?
        case START_ARRAY:
        case START_OBJECT:
        case FIELD_NAME:
            /* Output can be as JSON Object, Array or scalar: no way to know
             * a this point:
             */
            return toRuby(typeDeserializer.deserializeTypedFromAny(jp, ctxt));

        /* Otherwise we probably got a "native" type (ones that map
         * naturally and thus do not need or use type ids)
         */
        case VALUE_STRING:
            return rubyString(jp);

        case VALUE_NUMBER_INT:
            // For [JACKSON-100], see above:
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                return toRuby(jp.getBigIntegerValue());
            }
            /* and as per [JACKSON-839], allow "upgrade" to bigger types: out-of-range
             * entries can not be produced without type, so this should "just work",
             * even if it is bit unclean
             */
            return toRuby(jp.getNumberValue());

        case VALUE_NUMBER_FLOAT:
            // For [JACKSON-72], see above
            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                return toRuby(jp.getDecimalValue());
            }
            return __ruby__.newFloat(Double.valueOf(jp.getDoubleValue()));

        case VALUE_TRUE:
            return __ruby__.newBoolean(Boolean.TRUE);
        case VALUE_FALSE:
            return __ruby__.newBoolean(Boolean.FALSE);
        case VALUE_EMBEDDED_OBJECT:
            return toRuby(jp.getEmbeddedObject());

        case VALUE_NULL: // should not get this far really but...
            return null;
        default:
            throw ctxt.mappingException(Object.class);
        }
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */
    
    protected RubyObject toRuby(Object o) {
      return (RubyObject)JavaUtil.convertJavaToRuby(__ruby__, o);
    }

    protected RubyString rubyString(JsonParser jp) throws IOException {
      return __ruby__.newString(jp.getText());
    }

    /**
     * Method called to map a JSON Array into a Java value.
     */
    protected RubyObject mapArray(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
        //     return mapArrayToArray(jp, ctxt);
        // }
        // Minor optimization to handle small lists (default size for ArrayList is 10)
        if (jp.nextToken()  == JsonToken.END_ARRAY) {
            return RubyArray.newArray(__ruby__);
        }
        ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] values = buffer.resetAndStart();
        int ptr = 0;
        long totalSize = 0;
        do {
            Object value = deserialize(jp, ctxt);
            ++totalSize;
            if (ptr >= values.length) {
                values = buffer.appendCompletedChunk(values);
                ptr = 0;
            }
            values[ptr++] = value;
        } while (jp.nextToken() != JsonToken.END_ARRAY);
        // let's create almost full array, with 1/8 slack
        RubyArray result = RubyArray.newArray(__ruby__, (totalSize + (totalSize >> 3) + 1));
        buffer.completeAndClearBuffer(values, ptr, result);
        return result;
    }

    /**
     * Method called to map a JSON Object into a Java value.
     */
    protected RubyObject mapObject(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        // 1.6: minor optimization; let's handle 1 and 2 entry cases separately
        if (t != JsonToken.FIELD_NAME) { // and empty one too
            // empty map might work; but caller may want to modify... so better just give small modifiable
            return RubyHash.newHash(__ruby__);
        }
        RubyObject field1 = convertKey(jp);
        jp.nextToken();
        RubyObject value1 = deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.FIELD_NAME) { // single entry; but we want modifiable
            return RuntimeHelpers.constructHash(__ruby__, field1, value1);
        }
        RubyObject field2 = convertKey(jp);
        jp.nextToken();
        RubyObject value2 = deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            return RuntimeHelpers.constructHash(__ruby__, field1, value1, field2, value2);
        }
        // And then the general case; default map size is 16
        RubyHash result = RuntimeHelpers.constructHash(__ruby__, field1, value1, field2, value2);
        do {
            RubyObject fieldName = convertKey(jp);
            jp.nextToken();
            result.fastASet(fieldName, deserialize(jp, ctxt));
        } while (jp.nextToken() != JsonToken.END_OBJECT);
        return result;
    }

    /**
     * Method called to map a JSON Array into a Java Object array (Object[]).
     */
    // protected Object[] mapArrayToArray(JsonParser jp, DeserializationContext ctxt)
    //     throws IOException, JsonProcessingException
    // {
    //     // Minor optimization to handle small lists (default size for ArrayList is 10)
    //     if (jp.nextToken()  == JsonToken.END_ARRAY) {
    //         return NO_OBJECTS;
    //     }
    //     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
    //     Object[] values = buffer.resetAndStart();
    //     int ptr = 0;
    //     do {
    //         Object value = deserialize(jp, ctxt);
    //         if (ptr >= values.length) {
    //             values = buffer.appendCompletedChunk(values);
    //             ptr = 0;
    //         }
    //         values[ptr++] = value;
    //     } while (jp.nextToken() != JsonToken.END_ARRAY);
    //     return buffer.completeAndClearBuffer(values, ptr);
    // }
}

