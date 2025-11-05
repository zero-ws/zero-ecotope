package io.zerows.extension.skeleton.boot;

import io.r2mo.base.dbe.DBS;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zerows.cosmic.plugins.cache.SharedActor;
import io.zerows.cosmic.plugins.cache.SharedClient;
import io.zerows.epoch.store.DBSActor;
import io.zerows.extension.skeleton.exception._80214Exception417LoadingNotReady;
import io.zerows.platform.metadata.KTimer;
import io.zerows.plugins.excel.ExcelActor;
import io.zerows.plugins.excel.ExcelClient;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 替换旧版本中的静态模式下的 {@see Bt}，用于处理数据加载的专用操作，容器分两种模式
 * <pre><code>
 *     1. 本地模式，直接调用 {@link Ux#nativeVertx()} 获取 Vertx 实例
 *     2. 启动器模式，从外层传入容器获取 Vertx 实例
 * </code></pre>
 * 启动器作为底层处理执行程序，介于下边两个项目之间
 * <pre><code>
 *     1. zero-ifx-excel
 *     2. zero-focus
 * </code></pre>
 *
 * @author lang : 2023-06-12
 */
@Slf4j
public class DataImport {

    private static final Cc<Integer, DataImport> CC_IMPORT = Cc.open();

    private final Vertx vertx;

    private DataImport(final Vertx vertx) {
        this.vertx = vertx;
    }

    public static DataImport of(final Vertx vertx) {
        // 环境检查，防止导入问题，如果导入过程中没有准备好环境则直接抛出异常
        ensureEnvironment(vertx);
        // 构造导入器
        return CC_IMPORT.pick(() -> new DataImport(vertx), vertx.hashCode());
    }

    private static void ensureEnvironment(final Vertx vertx) {
        // 检查一：Jooq基础环境是否准备
        final DBS dbs = DBSActor.ofDBS();
        Fn.jvmKo(Objects.isNull(dbs), _80214Exception417LoadingNotReady.class, "database");
        // 检查二：Excel导入环境是否准备
        final ExcelClient excel = ExcelActor.ofClient();
        Fn.jvmKo(Objects.isNull(excel), _80214Exception417LoadingNotReady.class, "excel");
        // 检查三：Map导入环境是否准备
        final SharedClient shared = SharedActor.ofClient();
        Fn.jvmKo(Objects.isNull(shared), _80214Exception417LoadingNotReady.class, "shared");
        log.info("[ ZERO ] \uD83C\uDF89 数据导入环境检查通过，准备就绪！");
    }

    /**
     * （快速方法）异步加载数据，提供目录和 prefix 的匹配
     *
     * @param folder 目录
     * @param prefix 前缀
     *
     * @return {@link Future} 异步结果
     */
    public Future<Boolean> loadAsync(final String folder, final String prefix) {
        return this.future(folder, prefix, Boolean.FALSE);
    }

    public Future<Boolean> loadAsync(final String folder) {
        return this.future(folder, null, Boolean.FALSE);
    }

    /**
     * （快速方法）同步加载数据，提供目录和 prefix 的匹配
     *
     * @param folder 目录
     * @param prefix 前缀
     */
    public void load(final String folder, final String prefix) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, prefix, Boolean.FALSE)
            .onComplete(this.complete(folder, prefix, timer));
    }

    public void load(final String folder) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, null, Boolean.FALSE)
            .onComplete(this.complete(folder, null, timer));
    }

    /**
     * （快速方法）异步加载数据，提供目录和 prefix 的匹配，全称: Load And OOB
     *
     * @param folder 目录
     * @param prefix 前缀
     *
     * @return {@link Future} 异步结果
     */
    public Future<Boolean> loadWithAsync(final String folder, final String prefix) {
        return this.future(folder, prefix, Boolean.TRUE);
    }

    public Future<Boolean> loadWithAsync(final String folder) {
        return this.future(folder, null, Boolean.TRUE);
    }

    /**
     * （快速方法）同步加载数据，提供目录和 prefix 的匹配，全称: Load And OOB
     *
     * @param folder 目录
     * @param prefix 前缀
     */
    public void loadWith(final String folder, final String prefix) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, prefix, Boolean.TRUE)
            .onComplete(this.complete(folder, prefix, timer));
    }

    public void loadWith(final String folder) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, null, Boolean.TRUE)
            .onComplete(this.complete(folder, null, timer));
    }

    // -------------------- Private 私有化部分 -----------------------
    private Handler<AsyncResult<Boolean>> complete(final String folder, final String prefix, final KTimer timer) {
        return handler -> {
            if (handler.succeeded()) {
                if (Ut.isNil(prefix)) {
                    log.info("[ ZERO ] 数据目录 `{}` 导入成功！", folder);
                } else {
                    log.info("[ ZERO ] 数据目录 `{}` 匹配文件 `{}` 导入成功！", folder, prefix);
                }
                timer.end();
                log.info("[ ZERO ] 总执行时间 {}", timer.value());
                System.exit(0);
            } else {
                log.info(handler.cause().getMessage(), handler.cause());
                handler.cause().printStackTrace();
            }
        };
    }

    private Future<Boolean> future(final String folder, final String prefix, final boolean oob) {
        final List<Future<String>> files = new ArrayList<>();
        DataIo.ioFiles(folder, prefix, oob).map(this::execute).forEach(files::add);
        return FnBase.combineT(files).compose(nil -> Ux.future(Boolean.TRUE));
    }

    // 内部执行专用方法
    private Future<String> execute(final String filename) {
        return Ux.nativeWorker(filename, this.vertx, pre -> {
            final ExcelClient client = ExcelActor.ofClient();
            log.info("[ ZERO ] 开始导入文件：{}", filename);
            client.importAsync(filename, handler -> {
                if (handler.succeeded()) {
                    pre.complete(filename);
                } else {
                    pre.fail(handler.cause());
                }
            });
        });
    }
    // 完成专用方法
}
