package io.zerows.specification.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.atomic.HCommand;

/**
 * 「配置接口」
 * 针对组件的核心配置接口，重新设计配置层，保证所有组件都可以拥有一套基于某种规范的独立配置信息，此处的 HConfig 就是这种接口，
 * 当组件配置从此处继承时，可用于描述组件的核心 config 配置以实现基础独立配置信息，除开可管理的配置数据本身，还有特殊功能，
 * 即 Pre 组件针对配置的预处理器，Pre 组件可以在配置加载完成之后。
 * <pre>
 *     此处的双设计模型
 *     1. 原始配置 -> {@see UnitComopnent}，上层绑定到 yml 中的配置，这种配置执行过 lombok 中的 {@link lombok.Data}
 *        处理，所以是可直接序列化的内容，并且不包含任何抽象，此类仅包含结构
 *        {
 *            "component": "???",
 *            "config": {}
 *        }
 *     2. 当前接口中的实现 -> {@see ZeroConfig}，这是标准的 HConfig 实现，它将 Pre 流程引入配置处理中，最终编排成不同
 *        场景模式下的基础组件配置管理，二者呼应可实现整体结构和流程的统一。
 * </pre>
 * UnitComponent 的职责和 HConfig 不一样，HConfig 负责运行之后的配置最终组合，而 UnitComponent 只是单纯表示基本配置形态。
 *
 * @author lang : 2023-05-30
 */
public interface HConfig {

    /**
     * 返回组件原始配置
     *
     * @return {@link JsonObject}
     */
    default JsonObject options() {
        return new JsonObject();
    }

    /**
     * 设置组件原始配置
     *
     * @param options 配置数据
     *
     * @return {@link HConfig}
     */
    default HConfig options(final JsonObject options) {
        return this;
    }

    /**
     * 设置单个字段值，更改某个配置项相关信息
     *
     * @param field 字段名
     * @param value 字段值
     *
     * @return {@link HConfig}
     */
    default HConfig put(final String field, final Object value) {
        return this;
    }

    /**
     * 检查配置中是否存在某个字段
     *
     * @param field 字段名
     * @param <T>   返回值类型
     *
     * @return 存在返回 true，否则返回 false
     */
    default <T> T get(final String field) {
        return null;
    }

    /**
     *
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
         * 此方法可以直接菜单 HOn 引用中提取启动配置，由于 HOn 是单件模式，所以启动配置只有一份不会出现多份，基于此种设计，
         * 启动配置就可以直接存储在HOn组件中，而HOn组件又会被传入到 {@link HLauncher}中，那么真实启动器提取配置就会变得
         * 很简洁，而启动配置的初始化在调用 HOn 的 configure 方法中可执行完成。
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
