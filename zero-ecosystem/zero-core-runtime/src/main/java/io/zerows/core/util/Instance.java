package io.zerows.core.util;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VValue;
import io.zerows.ams.util.HUt;
import io.zerows.core.annotations.Contract;
import io.zerows.core.exception.BootingException;
import io.zerows.core.fn.FnZero;
import io.zerows.module.metadata.exception.BootDuplicatedImplException;
import io.zerows.module.metadata.exception._412ContractFieldException;
import io.zerows.module.metadata.store.OCacheClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Instance {
    /*
     * 「DEAD-LOCK」LoggerFactory.getLogger
     * Do not use `Annal` logger because of deadlock.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Instance.class);

    /*
     * 快速构造对象专用内存结构
     * Map ->  clazz = constructor length / true|false
     *         true  = 构造函数无长度型重载，只有一个方法
     *         false = 构造函数出现了重载
     */
    private static final ConcurrentMap<Class<?>, ConcurrentMap<Integer, Integer>> BUILD_IN =
        new ConcurrentHashMap<>();

    private Instance() {
    }

    /*
     * Enhancement for interface plugin initialized
     * 1) Get the string from `options[key]`
     * 2) Initialize the `key` string ( class name ) with interfaceCls
     */
    static <T> T plugin(final JsonObject options, final String key, final Class<?> interfaceCls) {
        if (HUt.isNil(options) || HUt.isNil(key)) {
            /*
             * options or key are either invalid
             */
            return null;
        }

        final String pluginClsName = options.getString(key);
        if (HUt.isNil(pluginClsName)) {
            /*
             * class name is "" or null
             */
            return null;
        }

        final Class<?> pluginCls = HUt.clazz(pluginClsName, null);
        if (Objects.isNull(pluginCls)) {
            /*
             * class could not be found.
             */
            return null;
        } else {
            if (HUt.isImplement(pluginCls, interfaceCls)) {
                return HUt.instance(pluginCls);
            } else {
                /*
                 * The class does not implement interface Cls
                 */
                return null;
            }
        }
    }

    /**
     * Find the unique implementation for interfaceCls
     */
    static Class<?> child(final Class<?> interfaceCls) {
        final Set<Class<?>> classes = OCacheClass.entireValue();
        final List<Class<?>> filtered = classes.stream()
            .filter(item -> interfaceCls.isAssignableFrom(item)
                && item != interfaceCls)
            .toList();
        final int size = filtered.size();
        // Non-Unique throw error out.
        if (VValue.ONE < size) {
            final BootingException error = new BootDuplicatedImplException(Instance.class, interfaceCls);
            LOGGER.error("[Tool] Error occurs {}", error.getMessage());
            throw error;
        }
        // Null means direct interface only.
        return VValue.ONE == size ? filtered.get(VValue.IDX) : null;
    }


    static Field[] fieldAll(final Object instance, final Class<?> fieldType) {
        final Function<Class<?>, Set<Field>> lookupFun = clazz -> lookUp(clazz, fieldType)
            .collect(Collectors.toSet());
        return fieldAll(instance.getClass(), fieldType).toArray(new Field[]{});
    }

    private static Set<Field> fieldAll(final Class<?> clazz, final Class<?> fieldType) {
        final Set<Field> fieldSet = new HashSet<>();
        if (Object.class != clazz) {

            /* Self */
            fieldSet.addAll(lookUp(clazz, fieldType).collect(Collectors.toSet()));

            /* Parent Iterator */
            fieldSet.addAll(fieldAll(clazz.getSuperclass(), fieldType));
        }
        return fieldSet;
    }

    static Field[] fields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
            .filter(item -> !Modifier.isStatic(item.getModifiers()))
            .filter(item -> !Modifier.isAbstract(item.getModifiers()))
            .toArray(Field[]::new);
    }

    private static Stream<Field> lookUp(final Class<?> clazz, final Class<?> fieldType) {
        return Fn.jvmOr(() -> {
            /* Lookup field */
            final Field[] fields = fields(clazz);
            /* Direct match */
            return Arrays.stream(fields)
                .filter(field -> fieldType == field.getType() ||          // Direct match
                    fieldType == field.getType().getSuperclass() ||  // Super
                    HUt.isImplement(field.getType(), fieldType));
        });
    }

    static <T> Field contract(final Class<?> executor, final T instance, final Class<?> fieldType) {
        /*
         * Reflect to set Api reference in target channel here
         * 1) The fields length must be 1
         * 2) The fields length must not be 0
         *  */
        final Field[] fields = fieldAll(instance, fieldType);
        /*
         * Counter
         */
        final Field[] filtered = Arrays.stream(fields)
            .filter(field -> field.isAnnotationPresent(Contract.class))
            .toArray(Field[]::new);
        FnZero.out(1 != filtered.length, _412ContractFieldException.class,
            executor, fieldType, instance.getClass(), filtered.length);
        return filtered[VValue.IDX];
    }

    static <T, V> void contract(final Class<?> executor, final T instance, final Class<?> fieldType, final V value) {
        final Field field = contract(executor, instance, fieldType);
        Ut.field(instance, field, value);
    }
}
