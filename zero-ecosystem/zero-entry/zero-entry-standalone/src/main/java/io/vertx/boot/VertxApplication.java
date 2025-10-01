package io.vertx.boot;

import io.vertx.boot.supply.Electy;
import io.vertx.core.Vertx;
import io.zerows.epoch.common.shared.boot.KLauncher;
import io.zerows.epoch.common.shared.context.KRunner;
import io.zerows.core.web.container.uca.store.StubLinear;
import io.zerows.epoch.enums.VertxComponent;
import io.zerows.specification.configuration.HConfig;

/**
 * 标准启动器，直接启动 Vertx 实例处理 Zero 相关的业务逻辑
 */
public class VertxApplication {

    public static void run(final Class<?> clazz, final String... args) {
        // 构造启动器容器
        final KLauncher<Vertx> container = KLauncher.create(clazz, args);
        container.start(Electy.whenContainer(VertxApplication::runInternal));
    }

    public static void runInternal(final Vertx vertx, final HConfig config) {

        /* Agent 类型处理新流程 */
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.AGENT), "component-agent");

        /* Worker 类型处理新流程 */
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.WORKER), "component-worker");

        /* Infusion 插件处理新流程  **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.INFUSION), "component-infix");

        /* Rule 验证规则处理流程 **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.CODEX), "component-codex");
    }
}
