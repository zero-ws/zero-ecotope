package io.zerows.boot.inst;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.boot.test.metadata.ArgLoad;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.jigsaw.NodePre;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;

/**
 * 「单应用」目前版本
 * 数据导入时参数必须要带，以确定当前参数是否包含 OOB 导入流程
 * <pre><code>
 * java -jar xxx.jar init/oop true
 * </code></pre>
 *
 * @author lang : 2023-06-10
 */
@Slf4j
public class LoadInst {

    public static void run(final Class<?> clazz, final String... args) {
        /*
         * 不做任何输入限制，都带有默认值处理
         * 0 - 导入数据的基本路径
         * 1 - 是否包含 OOB 数据
         * 2 - 是否带有 prefix 参数做文件过滤
         */
        final ArgLoad input = ArgLoad.of(args);
        final String path = input.value(KName.PATH);
        final Boolean oob = input.value("oob");
        final String prefix = input.value(KName.PREFIX);

        final String vPath = Ut.ioPath(path, input.environment());
        log.info("""
                信息说明
                \t环境：{}, 开启OOB：{}
                \t数据路径：{}
                \t过滤模式：{}
                """,
            input.environment(), oob, vPath, prefix
        );

        // 构造启动器（构造命令启动器）
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(clazz, args, NodePre::ensureDB);
        container.start((vertx, config) -> runApp(vertx).onComplete(res -> {
            log.info("[ ZERO ] ( LoadInst ) 应用初始化完成，开始执行数据导入...");
            runLoad(vertx, oob, vPath, prefix);
        }));
    }

    private static Future<Boolean> runApp(final Vertx vertx) {
        final List<URI> apps = InstApps.of().ioApp();
        // 应用导入

        // 菜单计算和导入
        return Future.succeededFuture(Boolean.TRUE);
    }

    private static void runLoad(final Vertx vertx,
                                final boolean oob,
                                final String vPath,
                                final String prefix) {
        return;
        // 构造数据导入器
//        final DataImport importer = DataImport.of(vertx);
//        if (oob) {
//            // 开启 OOB      ---> loadWith
//            importer.loadWith(vPath, prefix);
//        } else {
//            // 不开启 OOB   ---> load
//            importer.load(vPath, prefix);
//        }
    }
}
