package io.zerows.weaver;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.KView;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ZeroType {

    private static final Cc<String, Saber> CC_SABER = Cc.openThread();
    private static final ConcurrentMap<Class<?>, Supplier<Saber>> SABERS =
        new ConcurrentHashMap<>() {
            {
                this.put(int.class, ZeroType.supplier(SaberNumericInteger::new));
                this.put(Integer.class, ZeroType.supplier(SaberNumericInteger::new));
                this.put(short.class, ZeroType.supplier(SaberNumericShort::new));
                this.put(Short.class, ZeroType.supplier(SaberNumericShort::new));
                this.put(long.class, ZeroType.supplier(SaberNumericLong::new));
                this.put(Long.class, ZeroType.supplier(SaberNumericLong::new));

                this.put(double.class, ZeroType.supplier(SaberDecimalDouble::new));
                this.put(Double.class, ZeroType.supplier(SaberDecimalDouble::new));

                this.put(LocalDate.class, ZeroType.supplier(SaberJava8DataTime::new));
                this.put(LocalDateTime.class, ZeroType.supplier(SaberJava8DataTime::new));
                this.put(LocalTime.class, ZeroType.supplier(SaberJava8DataTime::new));

                this.put(float.class, ZeroType.supplier(SaberDecimalFloat::new));
                this.put(Float.class, ZeroType.supplier(SaberDecimalFloat::new));
                this.put(BigDecimal.class, ZeroType.supplier(SaberDecimalBig::new));

                this.put(Enum.class, ZeroType.supplier(SaberEnum::new));

                this.put(boolean.class, ZeroType.supplier(SaberBoolean::new));
                this.put(Boolean.class, ZeroType.supplier(SaberBoolean::new));

                this.put(Date.class, ZeroType.supplier(SaberDate::new));
                this.put(Calendar.class, ZeroType.supplier(SaberDate::new));

                this.put(JsonObject.class, ZeroType.supplier(SaberJsonObject::new));
                this.put(JsonArray.class, ZeroType.supplier(SaberJsonArray::new));

                this.put(String.class, ZeroType.supplier(SaberString::new));
                this.put(StringBuffer.class, ZeroType.supplier(SaberStringBuffer::new));
                this.put(StringBuilder.class, ZeroType.supplier(SaberStringBuffer::new));

                this.put(Buffer.class, ZeroType.supplier(SaberBuffer::new));
                this.put(Set.class, ZeroType.supplier(SaberCollection::new));
                this.put(List.class, ZeroType.supplier(SaberCollection::new));
                this.put(Collection.class, ZeroType.supplier(SaberCollection::new));

                this.put(byte[].class, ZeroType.supplier(SaberByteArray::new));
                this.put(Byte[].class, ZeroType.supplier(SaberByteArray::new));

                this.put(File.class, ZeroType.supplier(SaberFile::new));
                this.put(KView.class, ZeroType.supplier(SaberVis::new));
            }
        };

    private static Supplier<Saber> supplier(final Supplier<Saber> supplier) {
        final String cacheKey = "Saber@" + supplier.hashCode();
        return () -> CC_SABER.pick(supplier, cacheKey);
    }

    /**
     * String -> Tool
     *
     * @param paramType argument types
     * @param literal   literal values
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
                /*
                 * 修复旧代码
                 * Fix: Cannot invoke "java.util.function.Supplier.get()" because "supplier" is null
                 */
                saber = Objects.isNull(supplier) ? null : supplier.get();
            }
            if (null == saber) {
                saber = supplier(SaberCommon::new).get();
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
                        saber = supplier.get();
                    }
                } else {
                    final Supplier<Saber> supplier = SABERS.get(cls);
                    if (Objects.isNull(supplier)) {
                        log.warn("[ ZERO ] 无法找到 Saber 处理器：{}", cls);
                    }
                    saber = supplier.get();
                }
                if (null == saber) {
                    saber = supplier(SaberCommon::new).get();
                }
                reference = saber.from(input);
            }
            return reference;
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }
}
