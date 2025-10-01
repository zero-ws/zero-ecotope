package io.mature.boot.routine;

import io.vertx.boot.VertxApplication;
import io.vertx.boot.supply.Electy;
import io.vertx.core.Vertx;
import io.zerows.epoch.common.shared.boot.KLauncher;
import io.zerows.plugins.common.shell.ConsoleFramework;

/**
 * @author lang : 2023-06-12
 */
public class EngrossConsole {

    public static void run(final Class<?> clazz, final String[] args) {

        // 构造启动器（构造命令启动器）
        final KLauncher<Vertx> container = KLauncher.create(clazz, args);
        container.start(Electy.whenContainer((vertx, config) ->
            ConsoleFramework.start(vertx)
                .bind(command -> VertxApplication.runInternal(vertx, config))
                .run(args)
        ));
    }
}
