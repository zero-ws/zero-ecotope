package io.zerows.epoch.corpus.domain.uca.serialization;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.domain.atom.commune.Vis;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * ZeroType the request by different type.
 * 1. String -> Tool
 * 2. Tool -> JsonObject ( Envelop request )
 * 3. Tool -> String ( Generate resonse )
 * 4. Checking the request type to see where support serialization
 */
public class ZeroType {

    private static final OLog LOGGER = Ut.Log.ux(ZeroType.class);
    private static final Cc<String, Saber> CC_SABER = Cc.openThread();
    private static final ConcurrentMap<Class<?>, Supplier<Saber>> SABERS =
        new ConcurrentHashMap<>() {
            {
                this.put(int.class, ZeroType.supplier(IntegerSaber.class));
                this.put(Integer.class, ZeroType.supplier(IntegerSaber.class));
                this.put(short.class, ZeroType.supplier(ShortSaber.class));
                this.put(Short.class, ZeroType.supplier(ShortSaber.class));
                this.put(long.class, ZeroType.supplier(LongSaber.class));
                this.put(Long.class, ZeroType.supplier(LongSaber.class));

                this.put(double.class, ZeroType.supplier(DoubleSaber.class));
                this.put(Double.class, ZeroType.supplier(DoubleSaber.class));

                this.put(LocalDate.class, ZeroType.supplier(Java8DataTimeSaber.class));
                this.put(LocalDateTime.class, ZeroType.supplier(Java8DataTimeSaber.class));
                this.put(LocalTime.class, ZeroType.supplier(Java8DataTimeSaber.class));

                this.put(float.class, ZeroType.supplier(FloatSaber.class));
                this.put(Float.class, ZeroType.supplier(FloatSaber.class));
                this.put(BigDecimal.class, ZeroType.supplier(BigDecimalSaber.class));

                this.put(Enum.class, ZeroType.supplier(EnumSaber.class));

                this.put(boolean.class, ZeroType.supplier(BooleanSaber.class));
                this.put(Boolean.class, ZeroType.supplier(BooleanSaber.class));

                this.put(Date.class, ZeroType.supplier(DateSaber.class));
                this.put(Calendar.class, ZeroType.supplier(DateSaber.class));

                this.put(JsonObject.class, ZeroType.supplier(JsonObjectSaber.class));
                this.put(JsonArray.class, ZeroType.supplier(JsonArraySaber.class));

                this.put(String.class, ZeroType.supplier(StringSaber.class));
                this.put(StringBuffer.class, ZeroType.supplier(StringBufferSaber.class));
                this.put(StringBuilder.class, ZeroType.supplier(StringBufferSaber.class));

                this.put(Buffer.class, ZeroType.supplier(BufferSaber.class));
                this.put(Set.class, ZeroType.supplier(CollectionSaber.class));
                this.put(List.class, ZeroType.supplier(CollectionSaber.class));
                this.put(Collection.class, ZeroType.supplier(CollectionSaber.class));

                this.put(byte[].class, ZeroType.supplier(ByteArraySaber.class));
                this.put(Byte[].class, ZeroType.supplier(ByteArraySaber.class));

                this.put(File.class, ZeroType.supplier(FileSaber.class));
                this.put(Vis.class, ZeroType.supplier(VisSaber.class));
            }
        };

    private static Supplier<Saber> supplier(final Class<?> clazz) {
        return () -> CC_SABER.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    /**
     * String -> Tool
     *
     * @param paramType argument types
     * @param literal   literal values
     *
     * @return deserialized object.
     */
    public static Object value(final Class<?> paramType,
                               final String literal) {
        Object reference = null;
        if (null != literal) {
            Saber saber;
            if (paramType.isEnum()) {
                final Supplier<Saber> supplier = SABERS.get(Enum.class);
                saber = supplier.get();
            } else if (Collection.class.isAssignableFrom(paramType)) {
                final Supplier<Saber> supplier = SABERS.get(Collection.class);
                saber = supplier.get();
            } else {
                final Supplier<Saber> supplier = SABERS.get(paramType);
                if (Objects.isNull(supplier)) {
                    LOGGER.warn("The type {0} is not supported, will use default saber.", paramType.getName());
                }
                saber = supplier.get();
            }
            if (null == saber) {
                saber = supplier(CommonSaber.class).get();
            }
            reference = saber.from(paramType, literal);
        }
        return reference;
    }

    @SuppressWarnings("unchecked")
    public static <T> T valueT(final Class<?> paramType,
                               final String literal) {
        return (T) value(paramType, literal);
    }

    public static <T> boolean isSupport(final T input) {
        boolean result = false;
        if (null != input) {
            final Class<?> cls = input.getClass();
            if (JsonObject.class != cls && JsonArray.class != cls) {
                result = SABERS.containsKey(cls);
            }
        }
        return result;
    }

    /**
     * Tool -> JsonObject
     *
     * @param input Checked object
     * @param <T>   Generic Types
     *
     * @return returned values.
     */
    public static <T> Object valueSupport(final T input) {
        try {
            Object reference = null;
            if (null != input) {
                Saber saber;
                final Class<?> cls = input.getClass();
                if (cls.isEnum()) {
                    final Supplier<Saber> supplier = SABERS.get(Enum.class);
                    saber = supplier.get();
                } else if (Calendar.class.isAssignableFrom(cls)) {
                    final Supplier<Saber> supplier = SABERS.get(Date.class);
                    saber = supplier.get();
                } else if (Collection.class.isAssignableFrom(cls)) {
                    final Supplier<Saber> supplier = SABERS.get(Collection.class);
                    saber = supplier.get();
                } else if (Buffer.class.isAssignableFrom(cls)) {
                    final Supplier<Saber> supplier = SABERS.get(Buffer.class);
                    saber = supplier.get();
                } else if (cls.isArray()) {
                    final Class<?> type = cls.getComponentType();
                    if (byte.class == type || Byte.class == type) {
                        final Supplier<Saber> supplier = SABERS.get(byte[].class);
                        saber = supplier.get();
                    } else {
                        final Supplier<Saber> supplier = SABERS.get(Collection.class);
                        saber = supplier.get(); //  SABERS.get(Collection.class);
                    }
                } else {
                    final Supplier<Saber> supplier = SABERS.get(cls);
                    saber = supplier.get();
                }
                if (null == saber) {
                    saber = supplier(CommonSaber.class).get();
                }
                reference = saber.from(input);
            }
            return reference;
        } catch (final Throwable ex) {
            /*
             * Serialization debug for data
             */
            ex.printStackTrace();
            return null;
        }
    }
}
