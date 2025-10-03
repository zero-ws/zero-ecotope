package io.zerows.specification.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KConfig;
import io.zerows.specification.atomic.HCommand;

/**
 * 「配置接口」
 * 针对组件的核心配置接口，重新设计配置层
 *
 * @author lang : 2023-05-30
 */
public interface HConfig {
    /**
     * 默认配置
     *
     * @param options 配置信息
     *
     * @return {@link HConfig}
     */
    static HConfig of(final JsonObject options) {
        return new KConfig().options(options);
    }

    default JsonObject options() {
        return new JsonObject();
    }

    default HConfig options(final JsonObject options) {
        return this;
    }

    default HConfig put(final String field, final Object value) {
        return this;
    }

    default <T> T get(final String field) {
        return null;
    }

    default Class<?> pre() {
        return null;
    }

    default HConfig pre(final Class<?> preCls) {
        return this;
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HOff<T extends HConfig> extends HCommand<T, Boolean> {

        default T store() {
            return null;
        }
    }

    /**
     * 「指令」准入（底层抽象，负责检查）
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HOn<T extends HConfig> extends HCommand<T, Boolean> {
        /**
         * 此方法可以直接菜单 HOn 引用中提取启动配置，由于 HOn 是单件模式，所以
         * 启动配置只有一份不会出现多份，基于此种设计，启动配置就可以直接存储在
         * HOn组件中，而HOn组件又会被传入到 {@link HLauncher}
         * 中，那么真实启动器提取配置就会变得很简洁，而启动配置的初始化在
         * 调用 HOn 的 configure 方法中可执行完成。
         *
         * @return {@link T}
         */
        default T store() {
            return null;
        }

        /**
         * 启动参数绑定
         *
         * @param args 待绑定参数
         *
         * @return 当前引用
         */
        default HOn<T> args(final String[] args) {
            return this;
        }

        /**
         * 启动参数提取，根据参数索引提取参数
         *
         * @return 返回提取的参数值
         */
        String[] args();
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HRun<T extends HConfig> extends HCommand<T, Boolean> {
    }
}
