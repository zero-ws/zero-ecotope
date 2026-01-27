package io.zerows.extension.skeleton.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.web.MDConfig;
import io.zerows.specification.configuration.HConfig;

import java.util.function.Supplier;

/**
 * 配置转换特殊接口，将 {@link HConfig} 配置对象转换成当前扩展模块的专用配置对象
 * <pre>
 *     1. 对接基础配置 vertx.yml 中的系统级扩展配置
 *        {@link NodeStore} 中的方法用来查找配置信息
 *        - {@link NodeStore#findInfix} 用于处理原生插件：-plugins-
 *        - {@link NodeStore#findExtension} 用于处理扩展模块：-exmodule/extension-
 *
 *        ---> 上述两个配置都会直接返回 {@link HConfig} 对象，而当前接口就是将 {@link HConfig} 对象转换成模块专用配置对象。
 *
 *     2. 对接业务配置 plugins/{mid}/configuration.json 中的模块级配置
 * </pre>
 * 上述两块配置都从此处转换
 *
 * @author lang : 2025-12-22
 */
public interface MDSetting<YAML extends MDConfig> {

    Cc<String, MDSetting<?>> CC_SKELETON = Cc.openThread();

    @SuppressWarnings("all")
    static <T extends MDConfig> MDSetting<T> of(final Supplier<MDSetting<T>> constructorFn) {
        return (MDSetting<T>) CC_SKELETON.pick(constructorFn::get, String.valueOf(constructorFn.hashCode()));
    }

    YAML bootstrap(HConfig config, Vertx vertx);
}
