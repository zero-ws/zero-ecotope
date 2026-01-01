package io.zerows.sdk.plugins;

import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HConfig;

/**
 * Vertx 原生环境专用的 AddOn 抽象类，子类必须提供名称，并且使用此名称创建单例对象，这种模式用于不带 {@link AddOn.Name} 注解的场景。如果是
 * 自定义的 Client，不出意外的场景下一定会包含 {@link AddOn.Name} 注解，此时请直接继承自 {@link AddOnBase} 即可，若不是自定义 Client，
 * 如 Vertx 环境中的原生 Client ( Redis / Mongo ) 等，则子类应该继承当前类，才可以返回正确的单例对象。
 * <pre>
 *     备注：关于 DI 本身
 *     1. Actor 启动时做的事：
 *        - 拉起 Manager 单例初始化 DI 对象的管理器
 *        - 拉起 AddOn 单例初始化 DI 对象的提供器，并且使用 AddOn 绑定 {@link jakarta.inject.Provider} 的处理流程和机制
 *     2. 后续过程中无参调用：
 *        - XxxManager.of() -> 获取 Manager 单例
 *        - XxxAddOn.of() -> 获取 AddOn 单例
 *     3. 获取 DI 对象的流程：
 *        - XxxAddOn.createSingleton() -> 获取单例 DI 对象
 *        - XxxManager.createInstance(String) -> 创建 DI 对象，传入名称会和 {@link jakarta.inject.Named} 中名称绑定
 * </pre>
 *
 * @author lang : 2026-01-01
 */
public abstract class AddOnVertx<DI> extends AddOnBase<DI> {
    protected AddOnVertx(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    /**
     * 核心逻辑，更改 createSingleton 方法的默认行为，跳过 {@link AddOn.Name} 注解的处理，直接使用固定名称创建，此名称子类提供。
     *
     * @return 单例对象
     */
    @Override
    public DI createSingleton() {
        return this.manager().get(this.name(), this::createInstance);
    }

    protected abstract String name();
}
