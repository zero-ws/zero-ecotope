package io.zerows.specification.configuration;

import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.atomic.HCommand;

/**
 * 「配置接口」
 * 针对组件的核心配置接口，重新设计配置层，保证所有组件都可以拥有一套基于某种规范的独立配置信息，此处的 HConfig 就是这种接口，
 * 当组件配置从此处继承时，可用于描述组件的核心 config 配置以实现基础独立配置信息，除开可管理的配置数据本身，还有特殊功能，
 * 即 Executor 组件存储功能，存储的组件为 {@link Class} 类型，可直接反射获取。
 * <pre>
 *     此处的双设计模型
 *     1. 原始配置 -> {@see UnitComopnent}，上层绑定到 yml 中的配置，这种配置执行过 lombok 中的 {@link lombok.Data}
 *        处理，所以是可直接序列化的内容，并且不包含任何抽象，此类仅包含结构
 *        {
 *            "component": "???",
 *            "config": {}
 *        }
 *     2. 当前接口中的实现 -> {@see ZeroConfig}，这是标准的 HConfig 实现，此处的 executor() 会在两种场景种使用
 *        - 静态配置场景：UnitComponent 中的 component 字段会被存储到 executor 中，方便后期反射
 *        - 动态配置场景：通过代码直接调用 executor 方法保存和获取组件信息
 * </pre>
 * UnitComponent 的职责和 HConfig 不一样，HConfig 负责运行之后的配置最终组合，而 UnitComponent 只是单纯表示基本配置形态。
 *
 * @author lang : 2023-05-30
 */
public interface HConfig {

    // ------- Class<?> 反射
    String DEFAULT_META = "DEFAULT_META";
    // ----- 对象引用操作
    String DEFAULT_REFERENCE = "DEFAULT_REFERENCE";

    // --- 配置项操作
    JsonObject options();

    /**
     * 特殊方法，提供便捷的 vertx.yml 配置的读取能力，这种主要用于不同的配置对象读取
     * <pre>
     *     1. Zero 底层基于 {@see YmSpec} 的方式执行配置读取
     *     2. 不同的组件定义不同的配置类，在 HActor 启动时会自动加载节点
     *     3. 若定义了新的配置类，此方法可以将：yaml -> {@link JsonObject} -> 目标类 实现自动转换
     * </pre>
     *
     * @param classYm 目标类
     * @param <T>     返回值类型
     * @return 目标对象
     */
    <T> T options(Class<T> classYm);

    HConfig putOptions(JsonObject options);

    /**
     * 设置单个字段值，更改某个配置项相关信息
     *
     * @param field 字段名
     * @param value 字段值
     * @return {@link HConfig}
     */
    HConfig putOptions(String field, Object value);

    /**
     * 检查配置中是否存在某个字段
     *
     * @param field 字段名
     * @param <T>   返回值类型
     * @return 存在返回 true，否则返回 false
     */
    <T> T options(String field);

    <T> T options(String field, T defaultValue);

    /**
     * 设置配制键的反射组件，后期可直接反射得到结果
     *
     * @param configKey 配置键
     * @param clazz     组件类
     * @return Fluent 模式的自身引用
     */
    default HConfig putExecutor(final String configKey, final Class<?> clazz) {
        throw new _501NotSupportException("[ ZERO ] 当前 HConfig 不支持 putExecutor 方法，请检查配置！");
    }

    default HConfig putExecutor(final Class<?> clazz) {
        return this.putExecutor(DEFAULT_META, clazz);
    }

    /**
     * 根据配置键获取反射组件
     *
     * @param configKey 配置键
     * @return 组件类
     */
    default Class<?> executor(final String configKey) {
        return null;
    }

    default Class<?> executor() {
        return this.executor(DEFAULT_META);
    }

    default <T> HConfig putRef(final String refKey, final T reference) {
        throw new _501NotSupportException("[ ZERO ] 当前 HConfig 不支持 putRef 方法，请检查配置！");
    }

    default <T> HConfig putRef(final T reference) {
        return this.putRef(DEFAULT_REFERENCE, reference);
    }

    default <T> T ref(final String refKey) {
        return null;
    }

    default <T> T ref() {
        return this.ref(DEFAULT_REFERENCE);
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
