package io.zerows.support;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Contract;
import io.zerows.epoch.exception._60040Exception412ContractField;
import io.zerows.platform.constant.VValue;
import io.zerows.support.base.UtBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
        if (UtBase.isNil(options) || UtBase.isNil(key)) {
            /*
             * options or key are either invalid
             */
            return null;
        }

        final String pluginClsName = options.getString(key);
        if (UtBase.isNil(pluginClsName)) {
            /*
             * class name is "" or null
             */
            return null;
        }

        final Class<?> pluginCls = UtBase.clazz(pluginClsName, null);
        if (Objects.isNull(pluginCls)) {
            /*
             * class could not be found.
             */
            return null;
        } else {
            if (UtBase.isImplement(pluginCls, interfaceCls)) {
                return UtBase.instance(pluginCls);
            } else {
                /*
                 * The class does not implement interface Cls
                 */
                return null;
            }
        }
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
                    UtBase.isImplement(field.getType(), fieldType));
        });
    }

    static <T> Field contract(final Class<?> executor, final T instance, final Class<?> fieldType) {
        /*
         * Reflect to set Api reference in ofMain channel here
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
        Fn.jvmKo(1 != filtered.length, _60040Exception412ContractField.class, fieldType, instance.getClass(), filtered.length);
        return filtered[VValue.IDX];
    }

    static <T, V> void contract(final Class<?> executor, final T instance, final Class<?> fieldType, final V value) {
        final Field field = contract(executor, instance, fieldType);
        Ut.field(instance, field, value);
    }
}
