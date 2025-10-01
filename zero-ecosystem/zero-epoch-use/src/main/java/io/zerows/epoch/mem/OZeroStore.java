package io.zerows.epoch.mem;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.common.log.Log4JAnnal;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.spi.boot.HEquip;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2023-05-30
 */
public class OZeroStore {
    private static final HSetting SETTING;
    private static final ConcurrentMap<String, Class<?>> INJECTION = new ConcurrentHashMap<>();

    static {
        {
            // 设置读取
            final HEquip equip = OZeroEquip.of(null);
            SETTING = equip.initialize();

            final OCacheFailure failure = OCacheFailure.of(null);
            failure.configure(SETTING);
        }
        /*
         * vertx-inject.yml
         */
        final HConfig injectJ = SETTING.infix(YmlCore.inject.__KEY);
        Ut.<String>itJObject(injectJ.options(), (pluginCls, field) -> {
            if (!pluginCls.equals(Log4JAnnal.class.getName())) {
                Ut.Log.configure(OZeroStore.class).info(
                    "The inject config ( node = {0} ) has been detected plugin ( {1} = {2} )",
                    YmlCore.inject.__KEY, field, pluginCls);
            }
            INJECTION.put(field, Ut.clazz(pluginCls));
        });
    }

    public static <T> T option(final String infixKey, final Class<T> clazz, final Supplier<T> supplier) {
        final HConfig config = SETTING.infix(infixKey);
        if (Objects.isNull(config)) {
            return supplier.get();
        } else {
            final JsonObject options = config.options();
            final T created = Ut.deserialize(options, clazz, false);
            return Objects.isNull(created) ? supplier.get() : created;
        }
    }

    public static JsonObject option(final String infixKey) {
        final HConfig config = SETTING.infix(infixKey);
        if (Objects.isNull(config)) {
            return new JsonObject();
        } else {
            return config.options();
        }
    }

    public static boolean is(final String infixKey) {
        return SETTING.hasInfix(infixKey);
    }

    public static Class<?> classInject(final String field) {
        return INJECTION.get(field);
    }

    public static ConcurrentMap<String, Class<?>> classInject() {
        return INJECTION;
    }

    public static HSetting setting() {
        return SETTING;
    }

    /**
     * 注册机制，主要用于 Zero Extension 模块，可提取 {@link HConfig} 数据来构造扩展模块独有的配置
     *
     * @param extensionKey 扩展模块的唯一标识
     * @param config       扩展模块的配置
     */
    public static void register(final String extensionKey, final HConfig config) {
        SETTING.extension(extensionKey, config);
    }
}
