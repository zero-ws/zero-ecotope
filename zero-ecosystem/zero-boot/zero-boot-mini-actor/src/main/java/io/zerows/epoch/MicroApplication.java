package io.zerows.epoch;

import io.vertx.core.Vertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.epoch.boot.Electy;
import io.zerows.epoch.corpus.container.uca.store.StubLinear;
import io.zerows.platform.metadata.KLauncher;
import io.zerows.platform.metadata.KRunner;
import io.zerows.specification.configuration.HConfig;

/**
 * Vertx EmApp begin launcher for api gateway.
 * It's only used in Micro Service mode.
 */
public class MicroApplication {

    public static void run(final Class<?> clazz, final String... args) {
        // 构造启动器容器
        final KLauncher<Vertx> container = KLauncher.create(clazz, args);
        container.start(Electy.whenContainer(MicroApplication::runComponent));
    }

    private static void runComponent(final Vertx vertx, final HConfig config) {
        /* 1.Find Agent for deploy **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.IPC), "component-gateway");
        /* 2.Find Worker for deploy **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.WORKER), "component-worker");
        /* 3.Initialize Infusion **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.INFUSION), "component-infix");
    }
}
