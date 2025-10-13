package io.zerows.specification.configuration;

import io.vertx.core.Future;

/**
 * 插件启动入口配置，会根据 {@link HSetting} 中配置的插件信息来提取相关配置，以保证整体插件配置的一致性，启动过程属于并行模式，类似
 * <pre>
 *     1. Launcher -> Actor-01
 *                 -> Actor-02
 *                 -> Actor-03
 *                 -> ...
 *                 -> Actor-N
 *     2. Actor 有三种
 *        - Internal：内部插件、启动 Zero 容器必须启动
 *        - Equip：外部插件、可选启动
 *        - Extension：扩展模块、带业务和表结构（复杂启动）
 * </pre>
 *
 * @author lang : 2025-10-08
 */
public interface HActor {

    <T> Future<Boolean> startAsync(HConfig config, T vertxRef);

    default <T> Future<Boolean> stopAsync(final HConfig config, final T vertxRef) {
        return Future.succeededFuture(Boolean.FALSE);
    }
}
