package io.zerows.component.serialization;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.metadata.commune.Vis;
import io.zerows.support.Ut;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
                this.put(int.class, ZeroType.supplier(SaberNumericInteger.class));
                this.put(Integer.class, ZeroType.supplier(SaberNumericInteger.class));
                this.put(short.class, ZeroType.supplier(SaberNumericShort.class));
                this.put(Short.class, ZeroType.supplier(SaberNumericShort.class));
                this.put(long.class, ZeroType.supplier(SaberNumericLong.class));
                this.put(Long.class, ZeroType.supplier(SaberNumericLong.class));

                this.put(double.class, ZeroType.supplier(SaberDecimalDouble.class));
                this.put(Double.class, ZeroType.supplier(SaberDecimalDouble.class));

                this.put(LocalDate.class, ZeroType.supplier(SaberJava8DataTime.class));
                this.put(LocalDateTime.class, ZeroType.supplier(SaberJava8DataTime.class));
                this.put(LocalTime.class, ZeroType.supplier(SaberJava8DataTime.class));

                this.put(float.class, ZeroType.supplier(SaberDecimalFloat.class));
                this.put(Float.class, ZeroType.supplier(SaberDecimalFloat.class));
                this.put(BigDecimal.class, ZeroType.supplier(SaberDecimalBig.class));

                this.put(Enum.class, ZeroType.supplier(SaberEnum.class));

                this.put(boolean.class, ZeroType.supplier(SaberBoolean.class));
                this.put(Boolean.class, ZeroType.supplier(SaberBoolean.class));

                this.put(Date.class, ZeroType.supplier(SaberDate.class));
                this.put(Calendar.class, ZeroType.supplier(SaberDate.class));

                this.put(JsonObject.class, ZeroType.supplier(SaberJsonObject.class));
                this.put(JsonArray.class, ZeroType.supplier(SaberJsonArray.class));

                this.put(String.class, ZeroType.supplier(SaberString.class));
                this.put(StringBuffer.class, ZeroType.supplier(SaberStringBuffer.class));
                this.put(StringBuilder.class, ZeroType.supplier(SaberStringBuffer.class));

                this.put(Buffer.class, ZeroType.supplier(SaberBuffer.class));
                this.put(Set.class, ZeroType.supplier(SaberCollection.class));
                this.put(List.class, ZeroType.supplier(SaberCollection.class));
                this.put(Collection.class, ZeroType.supplier(SaberCollection.class));

                this.put(byte[].class, ZeroType.supplier(SaberByteArray.class));
                this.put(Byte[].class, ZeroType.supplier(SaberByteArray.class));

                this.put(File.class, ZeroType.supplier(SaberFile.class));
                this.put(Vis.class, ZeroType.supplier(SaberVis.class));
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
                saber = supplier(SaberCommon.class).get();
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
                    saber = supplier(SaberCommon.class).get();
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
