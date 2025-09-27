package io.zerows.extension.runtime.skeleton.boot.supply;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zerows.common.program.KTimer;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.database.jooq.JooqInfix;
import io.zerows.core.fn.RFn;
import io.zerows.core.util.Ut;
import io.zerows.core.web.cache.shared.MapInfix;
import io.zerows.core.web.cache.shared.SharedClient;
import io.zerows.extension.runtime.skeleton.exception._417LoadingNotReadyException;
import io.zerows.plugins.office.excel.ExcelClient;
import io.zerows.plugins.office.excel.ExcelInfix;
import io.zerows.unity.Ux;
import org.jooq.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.zerows.extension.runtime.skeleton.refine.Ke.LOG;

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
public class DataImport {

    private static final Cc<Integer, DataImport> CC_IMPORT = Cc.open();

    private final Vertx vertx;

    private DataImport(final Vertx vertx) {
        this.vertx = vertx;
    }

    public static DataImport of(final Vertx vertx) {
        // 环境检查，防止导入问题，如果导入过程中没有准备好环境则直接抛出异常
        ensureEnvironment();
        // 构造导入器
        return CC_IMPORT.pick(() -> new DataImport(vertx), vertx.hashCode());
    }

    public static DataImport of() {
        // 环境检查，防止导入问题，如果导入过程中没有准备好环境则直接抛出异常
        ensureEnvironment();
        // 构造导入器
        final Vertx vertx = Ux.nativeVertx();
        return CC_IMPORT.pick(() -> new DataImport(vertx), vertx.hashCode());
    }

    private static void ensureEnvironment() {
        // 检查一：Jooq基础环境是否准备
        final Configuration jooq = JooqInfix.get(YmlCore.jooq.PROVIDER);
        RFn.outWeb(Objects.isNull(jooq), _417LoadingNotReadyException.class, DataImport.class, "jooq / provider");
        // 检查二：Excel导入环境是否准备
        final ExcelClient excel = ExcelInfix.getClient();
        RFn.outWeb(Objects.isNull(excel), _417LoadingNotReadyException.class, DataImport.class, "excel");
        // 检查三：Map导入环境是否准备
        final SharedClient<String, Object> shared = MapInfix.getClient();
        RFn.outWeb(Objects.isNull(shared), _417LoadingNotReadyException.class, DataImport.class, "shared");
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
    public Future<Boolean> landAsync(final String folder, final String prefix) {
        return this.future(folder, prefix, Boolean.TRUE);
    }

    public Future<Boolean> landAsync(final String folder) {
        return this.future(folder, null, Boolean.TRUE);
    }

    /**
     * （快速方法）同步加载数据，提供目录和 prefix 的匹配，全称: Load And OOB
     *
     * @param folder 目录
     * @param prefix 前缀
     */
    public void land(final String folder, final String prefix) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, prefix, Boolean.TRUE)
            .onComplete(this.complete(folder, prefix, timer));
    }

    public void land(final String folder) {
        final KTimer timer = KTimer.of().start();
        this.future(folder, null, Boolean.TRUE)
            .onComplete(this.complete(folder, null, timer));
    }

    // -------------------- Private 私有化部分 -----------------------
    private Handler<AsyncResult<Boolean>> complete(final String folder, final String prefix, final KTimer timer) {
        return handler -> {
            if (handler.succeeded()) {
                if (Ut.isNil(prefix)) {
                    LOG.Ke.info(this.getClass(), "The data folder `{0}` has been imported successfully!", folder);
                } else {
                    LOG.Ke.info(this.getClass(), "The data folder `{0}` with `{1}` has been imported successfully!", folder, prefix);
                }
                timer.end();
                LOG.Ke.info(this.getClass(), "TOTAL EXECUTION TIME = The total execution time = {0}!", timer.value());
                System.exit(0);
            } else {
                handler.cause().printStackTrace();
            }
        };
    }

    private Future<Boolean> future(final String folder, final String prefix, final boolean oob) {
        final List<Future<String>> files = new ArrayList<>();
        DataIo.ioFiles(folder, prefix, oob).map(this::execute).forEach(files::add);
        return RFn.combineT(files).compose(nil -> Ux.future(Boolean.TRUE));
    }

    // 内部执行专用方法
    private Future<String> execute(final String filename) {
        return Ux.nativeWorker(filename, this.vertx, pre -> {
            final ExcelClient client = ExcelInfix.createClient();
            LOG.Ke.info(this.getClass(), "Excel importing file = {0}", filename);
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
