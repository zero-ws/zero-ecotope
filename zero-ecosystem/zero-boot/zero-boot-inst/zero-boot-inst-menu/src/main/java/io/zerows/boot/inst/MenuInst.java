package io.zerows.boot.inst;

import io.vertx.core.Vertx;
import io.zerows.boot.test.metadata.ArgMenu;
import io.zerows.boot.test.metadata.QSiteMap;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * 「单应用」目前版本
 *
 * @author lang : 2023-06-12
 */
@Slf4j
public class MenuInst {
    public static void run(final Class<?> clazz, final String... args) {

        // 构造启动器（构造命令启动器）
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(clazz, args);
        container.start((vertx, config) -> {
            /*
             * 不做任何输入限制，都带有默认值处理
             */
            final ArgMenu input = ArgMenu.of(args);
            final String path = input.value(KName.PATH);
            final String vPath = Ut.ioPath(path, input.environment());
            log.info("""
                    信息说明
                    \t环境：{}
                    \t数据路径：{}
                    """,
                input.environment(), vPath
            );

            // 路由规划器
            QSiteMap.planOn(vPath).onComplete(res -> {
                if (res.result()) {
                    log.info("[ INST ] 菜单规划完成！");
                    System.exit(0);
                } else {
                    log.error(res.cause().getMessage(), res.cause());
                    System.exit(1);
                }
            });
        });
    }
}
