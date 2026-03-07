package io.zerows.boot.extension.appcontainer;

import io.vertx.core.Vertx;
import io.zerows.epoch.boot.ZeroLauncher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppInstPerm {

    public static void exec(final Class<?> target, final String[] args) {
        // 构造启动器（构造命令启动器）
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(target, args);
        container.start((vertx, config) -> {
            // 路由规划器
            BuildPerm.run(vertx).onComplete(res -> {
                log.info("[ ZERO ] ( LoadInst ) 权限设置处理完成...");
                System.exit(0);
            });
        });
    }
}
