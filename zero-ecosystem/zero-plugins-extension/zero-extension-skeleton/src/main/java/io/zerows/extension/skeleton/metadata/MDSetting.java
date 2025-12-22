package io.zerows.extension.skeleton.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HConfig;

import java.util.function.Supplier;

/**
 * 配置转换特殊接口，将 {@link HConfig} 配置对象转换成当前扩展模块的专用配置对象
 *
 * @author lang : 2025-12-22
 */
public interface MDSetting<CONFIG> {

    Cc<String, MDSetting<?>> CC_SKELETON = Cc.openThread();

    @SuppressWarnings("all")
    static <T> MDSetting<T> of(final Supplier<MDSetting<T>> constructorFn) {
        return (MDSetting<T>) CC_SKELETON.pick(constructorFn::get, String.valueOf(constructorFn.hashCode()));
    }

    CONFIG bootstrap(HConfig config, Vertx vertx);
}
