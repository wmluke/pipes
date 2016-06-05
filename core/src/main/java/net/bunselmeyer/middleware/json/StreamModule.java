package net.bunselmeyer.middleware.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamModule extends SimpleModule {
    public StreamModule() {
        super(StreamModule.class.getSimpleName());
        addSerializer(LongStream.class, new LongStreamSerializer());
        addSerializer(IntStream.class, new IntStreamSerializer());
        addSerializer(DoubleStream.class, new DoubleStreamSerializer());
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        context.addSerializers(new StreamSerializers());
        super.setupModule(context);
    }

    public static class StreamSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            Class<?> raw = type.getRawClass();
            if (Stream.class.isAssignableFrom(raw)) {
                JavaType[] params = config.getTypeFactory().findTypeParameters(type, Stream.class);
                JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
                return new StreamSerializer<Object>(config.getTypeFactory()
                    .constructParametricType(Stream.class, vt), vt);
            }
            return super.findSerializer(config, type, beanDesc);
        }
    }

    static class StreamSerializer<T> extends StdSerializer<Stream<T>> implements ContextualSerializer {
        private final JavaType streamType;
        private final JavaType elemType;

        public StreamSerializer(JavaType streamType, JavaType elemType) {
            super(streamType);
            this.streamType = streamType;
            this.elemType = elemType;
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            if (!elemType.hasRawClass(Object.class) && (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || elemType.isFinal())) {
                JsonSerializer<Object> elemSerializer = provider.findPrimaryPropertySerializer(elemType, property);
                return new TypedStreamSerializer<T>(streamType, elemSerializer);
            }
            return this;
        }

        @Override
        public void serialize(Stream<T> stream, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
            jgen.writeStartArray();
            try {
                stream.forEachOrdered(elem -> {
                    try {
                        provider.defaultSerializeValue(elem, jgen);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw (IOException) e.getCause();
            }
            jgen.writeEndArray();
        }
    }

    static class TypedStreamSerializer<T> extends StdSerializer<Stream<T>> {
        private final JsonSerializer<T> elemSerializer;

        @SuppressWarnings("unchecked")
        public TypedStreamSerializer(JavaType streamType, JsonSerializer<?> elemSerializer) {
            super(streamType);
            this.elemSerializer = (JsonSerializer<T>) elemSerializer;
        }

        @Override
        public void serialize(Stream<T> stream, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
            jgen.writeStartArray();
            try {
                stream.forEachOrdered(elem -> {
                    try {
                        elemSerializer.serialize(elem, jgen, provider);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw (IOException) e.getCause();
            }
            jgen.writeEndArray();
        }
    }

    static class IntStreamSerializer extends StdSerializer<IntStream> {
        public IntStreamSerializer() {
            super(IntStream.class);
        }

        @Override
        public void serialize(IntStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
            jgen.writeStartArray();
            try {
                stream.forEachOrdered(value -> {
                    try {
                        jgen.writeNumber(value);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw (IOException) e.getCause();
            }
            jgen.writeEndArray();
        }
    }

    static class LongStreamSerializer extends StdSerializer<LongStream> {
        public LongStreamSerializer() {
            super(LongStream.class);
        }

        @Override
        public void serialize(LongStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
            jgen.writeStartArray();
            try {
                stream.forEachOrdered(value -> {
                    try {
                        jgen.writeNumber(value);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw (IOException) e.getCause();
            }
            jgen.writeEndArray();
        }
    }

    static class DoubleStreamSerializer extends StdSerializer<DoubleStream> {
        public DoubleStreamSerializer() {
            super(DoubleStream.class);
        }

        @Override
        public void serialize(DoubleStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
            jgen.writeStartArray();
            try {
                stream.forEachOrdered(value -> {
                    try {
                        jgen.writeNumber(value);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw (IOException) e.getCause();
            }
            jgen.writeEndArray();
        }
    }

    public static final class WrappedIOException extends RuntimeException {
        private WrappedIOException(IOException e) {
            super(e);
        }
    }
}