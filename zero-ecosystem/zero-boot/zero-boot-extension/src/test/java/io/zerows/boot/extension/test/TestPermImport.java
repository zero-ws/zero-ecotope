package io.zerows.boot.extension.test;

import io.vertx.core.Vertx;
import io.zerows.boot.extension.appcontainer.BuildPerm;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.jigsaw.NodePre;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限导入测试程序
 * 用于验证 BuildPerm 的导入功能
 */
@Slf4j
public class TestPermImport {

    public static void main(final String[] args) {
        log.info("========================================");
        log.info("开始测试权限导入功能");
        log.info("========================================");

        // 构造启动器
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(TestPermImport.class, args, NodePre::ensureDB);
        container.start((vertx, config) -> {
            // 执行权限导入
            BuildPerm.run(vertx).onComplete(res -> {
                if (res.succeeded() && res.result()) {
                    log.info("========================================");
                    log.info("权限导入测试完成 - 成功");
                    log.info("========================================");
                } else {
                    log.error("========================================");
                    log.error("权限导入测试完成 - 失败");
                    log.error("========================================");
                }
                System.exit(res.succeeded() && res.result() ? 0 : 1);
            });
        });
    }
}
