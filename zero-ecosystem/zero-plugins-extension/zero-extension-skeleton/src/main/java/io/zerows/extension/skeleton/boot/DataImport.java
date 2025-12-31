package io.zerows.extension.skeleton.boot;

import io.r2mo.base.dbe.DBS;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zerows.epoch.store.DBSActor;
import io.zerows.extension.skeleton.exception._80214Exception417LoadingNotReady;
import io.zerows.platform.metadata.KTimer;
import io.zerows.plugins.cache.SharedActor;
import io.zerows.plugins.cache.SharedClient;
import io.zerows.plugins.excel.ExcelActor;
import io.zerows.plugins.excel.ExcelClient;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
        log.info("[ INST ] \uD83C\uDF89 数据导入环境检查通过，准备就绪！");
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
                    log.info("[ INST ] 数据目录 `{}` 导入成功！", folder);
                } else {
                    log.info("[ INST ] 数据目录 `{}` 匹配文件 `{}` 导入成功！", folder, prefix);
                }
                timer.end();
                log.info("[ INST ] 总执行时间 {}", timer.value());
                System.exit(0);
            } else {
                log.error(handler.cause().getMessage(), handler.cause());
                try {
                    // 给异步日志框架一点时间将缓冲区（Buffer）中的内容推送到终端或文件
                    // 如果在 log.error 之后立即执行 System.exit(1)，JVM 会瞬间关闭，此时日志缓冲区里的表格信息可能只打印了一半，甚至完全没打出来程序就死了。
                    Thread.sleep(200);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.exit(1);
            }
        };
    }

    private Future<Boolean> future(final String folder, final String prefix, final boolean oob) {
        final List<Future<String>> files = new ArrayList<>();
        final ConcurrentMap<String, String> failureMap = new ConcurrentHashMap<>();

        DataIo.ioFiles(folder, prefix, oob)
            .map(filename -> this.execute(filename, failureMap))
            .forEach(files::add);

        return FnBase.combineT(files)
            .map(nil -> Boolean.TRUE)
            .recover(error -> {
                if (!failureMap.isEmpty()) {
                    // 1. 计算 Message 的最大长度（用于对齐），设定最小宽度为 20
                    final int maxMessageLen = failureMap.values().stream()
                        .mapToInt(String::length)
                        .max()
                        .orElse(20);

                    // 2. 构造格式化字符串
                    final StringBuilder sb = new StringBuilder();
                    sb.append("\n\n").append("=".repeat(maxMessageLen + 50));
                    sb.append(String.format("\n📊 数据导入异常汇总 (失败文件数: %d)\n", failureMap.size()));
                    sb.append("-".repeat(maxMessageLen + 50)).append("\n");

                    // 表头
                    final String headerFormat = "   %-" + maxMessageLen + "s   |   %s\n";
                    sb.append(String.format(headerFormat, "❌ 错误信息 (Message)", "📄 文件名 (File)"));
                    sb.append("-".repeat(maxMessageLen + 50)).append("\n");

                    // 内容行
                    final String rowFormat = "   %-" + maxMessageLen + "s   |   %s\n";
                    failureMap.forEach((file, message) -> {
                        // 处理换行符，防止破坏表格结构
                        final String cleanMsg = message.replace("\n", " ").trim();
                        sb.append(String.format(rowFormat, cleanMsg, file));
                    });

                    sb.append("=".repeat(maxMessageLen + 50)).append("\n");

                    // 3. 一次性打印输出
                    log.error(sb.toString());
                }

                return Future.failedFuture(error);
            });
    }

    // 内部执行专用方法
    private Future<String> execute(final String filename, final ConcurrentMap<String, String> failureMap) {
        return Ux.nativeWorker(filename, this.vertx, pre -> {
            try {
                final ExcelClient client = ExcelActor.ofClient();
                log.info("[ INST ] 开始导入文件：{}", filename);
                client.importAsync(filename, handler -> {
                    if (handler.succeeded()) {
                        pre.complete(filename);
                    } else {
                        // 路径 A：异步执行失败
                        failureMap.put(filename, handler.cause().getMessage());
                        pre.fail(handler.cause());
                    }
                });
            } catch (final Throwable ex) {
                // 路径 B：同步调用失败（如 importAsync 方法本身报错）
                // 这里的统计必须补上！
                failureMap.put(filename, ex.getMessage());
                pre.fail(ex);
            }
        });
    }
}
