package io.mature.boot.routine;

import io.mature.boot.argument.ArgLoad;
import io.vertx.core.Vertx;
import io.zerows.core.constant.KName;
import io.zerows.epoch.common.shared.boot.KLauncher;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.skeleton.boot.supply.DataImport;

import static io.zerows.extension.runtime.skeleton.refine.Ke.LOG;

/**
 * 「单应用」目前版本
 * 数据导入时参数必须要带，以确定当前参数是否包含 OOB 导入流程
 * <pre><code>
 * java -jar xxx.jar init/oop true
 * </code></pre>
 *
 * @author lang : 2023-06-10
 */
public class EngrossLoad {

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
        LOG.Ke.info(clazz, """
                信息说明
                \t环境：{0}, 开启OOB：{1}
                \t数据路径：{2}
                \t过滤模式：{3}
                """,
            input.environment(), oob, vPath, prefix
        );

        // 构造启动器（构造命令启动器）
        final KLauncher<Vertx> container = KLauncher.create(clazz, args);
        container.start((vertx, config) -> {
            // 构造数据导入器
            final DataImport importer = DataImport.of(vertx);
            if (oob) {
                // 开启 OOB      ---> land
                importer.land(vPath, prefix);
            } else {
                // 不开启 OOB   ---> load
                importer.load(vPath, prefix);
            }
        });
    }
}
