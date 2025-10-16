package io.zerows.cosmic.handler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public final class ZeroWatchDog {

    private ZeroWatchDog() {
    }

    /* ===== 默认参数 ===== */
    private static final long DEFAULT_TIMEOUT_MS = 5_000L;
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final long DEFAULT_BACKOFF_MS = 300L;
    private static final Duration DEFAULT_DUMP_BUDGET = Duration.ofMillis(800);

    /**
     * 为 Future 增加超时保护（默认 5s）。会打印：
     * 1) 看门狗超时提示
     * 2) “发起调用堆栈”（origin stack）——包裹时捕获
     * 3) 全量线程转储（限时）
     */
    @CanIgnoreReturnValue
    public static <T> Future<T> watchAsync(
        final Vertx vertx,
        final Future<T> origin,
        final String name
    ) {
        Objects.requireNonNull(vertx, "[ ZERO ] Vertx 不能为空");
        Objects.requireNonNull(origin, "[ ZERO ] origin Future 不能为空");

        final Promise<T> p = Promise.promise();

        // 关键：在包裹的那一刻捕获“调用点堆栈”
        final Throwable originStack = new Throwable("[ ZERO ] 异步调用发起位置 -> " + name);

        final long startNs = System.nanoTime();
        final long timerId = vertx.setTimer(DEFAULT_TIMEOUT_MS, tid -> {
            final long elapsed = (System.nanoTime() - startNs) / 1_000_000;
            log.error("[ ZERO ] 看门狗超时（>{}ms）-> {}，elapsed={}ms", DEFAULT_TIMEOUT_MS, name, elapsed);

            // 先打发起调用堆栈（最有用）
            log.error("[ ZERO ] 发起调用堆栈（origin stack）如下：", originStack);

            // 再打线程转储（用于发现其他阻塞点）
            log.error("[ ZERO ] 线程转储开始 >>>>>>>>>>>>");
            log.error("\n{}", dumpAllThreads());
            log.error("[ ZERO ] 线程转储结束 <<<<<<<<<<<<");

            p.tryFail(new IllegalStateException("[ ZERO ] timeout: " + name + ", " + DEFAULT_TIMEOUT_MS + "ms", originStack));
        });

        origin.onComplete(ar -> {
            vertx.cancelTimer(timerId);
            if (ar.succeeded()) {
                p.tryComplete(ar.result());
            } else {
                p.tryFail(ar.cause());
            }
        });

        return p.future();
    }

    /**
     * 带重试（默认：总共 3 次，每次 5s，间隔 300ms）。同样打印 origin stack。
     */
    public static <T> Future<T> watchAsyncRetry(
        final Vertx vertx,
        final Supplier<Future<T>> supplier,
        final String name
    ) {
        Objects.requireNonNull(vertx, "[ ZERO ] Vertx 不能为空");
        Objects.requireNonNull(supplier, "[ ZERO ] supplier 不能为空");

        final Promise<T> p = Promise.promise();

        // 捕获“第一次发起位置”的堆栈（重试沿用这份）
        final Throwable originStack = new Throwable("[ ZERO ] 异步调用发起位置（含重试链）-> " + name);

        class Attempt {
            int n = 0; // 0..DEFAULT_MAX_RETRIES

            void go() {
                final String phase = name + "(attempt#" + (this.n + 1) + ")";
                log.debug("[ ZERO ] 看门狗重试包装：开始 {}", phase);

                final Future<T> one = safeGet(supplier, phase);
                if (one == null) {
                    p.tryFail(new IllegalStateException("[ ZERO ] supplier 返回了 null Future: " + phase));
                    return;
                }

                final long startNs = System.nanoTime();
                final long timerId = vertx.setTimer(DEFAULT_TIMEOUT_MS, tid -> {
                    final long elapsed = (System.nanoTime() - startNs) / 1_000_000;
                    log.warn("[ ZERO ] 看门狗单次尝试超时（>{}ms）-> {}，elapsed={}ms，第 {}/{} 次",
                        DEFAULT_TIMEOUT_MS, phase, elapsed, this.n + 1, DEFAULT_MAX_RETRIES + 1);
                    log.error("[ ZERO ] 发起调用堆栈（origin stack）如下：", originStack);
                    log.error("[ ZERO ] 线程转储开始 >>>>>>>>>>>>\n{}\n[ ZERO ] 线程转储结束 <<<<<<<<<<<<", dumpAllThreads());
                    // 超时按失败处理，进入重试逻辑
                    if (this.n < DEFAULT_MAX_RETRIES) {
                        this.n++;
                        vertx.setTimer(DEFAULT_BACKOFF_MS, id -> this.go());
                    } else {
                        p.tryFail(new IllegalStateException("[ ZERO ] timeout: " + phase, originStack));
                    }
                });

                one.onComplete(ar -> {
                    vertx.cancelTimer(timerId);
                    if (ar.succeeded()) {
                        p.tryComplete(ar.result());
                    } else {
                        log.warn("[ ZERO ] 看门狗：{} 失败，第 {}/{} 次，原因={}",
                            phase, this.n + 1, DEFAULT_MAX_RETRIES + 1, ar.cause().toString());
                        if (this.n < DEFAULT_MAX_RETRIES) {
                            this.n++;
                            vertx.setTimer(DEFAULT_BACKOFF_MS, id -> this.go());
                        } else {
                            p.tryFail(ar.cause());
                        }
                    }
                });
            }
        }

        new Attempt().go();
        return p.future();
    }

    /* ==================== 内部辅助 ==================== */

    private static <T> Future<T> safeGet(final Supplier<Future<T>> supplier, final String phase) {
        try {
            return supplier.get();
        } catch (final Throwable e) {
            log.error("[ ZERO ] supplier.get() 抛出异常 -> {}，异常={}", phase, e.toString());
            log.debug("[ ZERO ] supplier 异常堆栈：", e);
            return Future.failedFuture(e);
        }
    }

    /** 收集所有线程栈（带时间预算，防刷屏）。 */
    private static String dumpAllThreads() {
        final long deadline = System.nanoTime() + DEFAULT_DUMP_BUDGET.toNanos();
        final StringBuilder sb = new StringBuilder(8192);
        try {
            for (final Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
                final Thread t = e.getKey();
                final StackTraceElement[] trace = e.getValue();
                sb.append("线程[").append(t.getName())
                    .append("] id=").append(t.threadId())
                    .append(" state=").append(t.getState())
                    .append(" daemon=").append(t.isDaemon())
                    .append('\n');
                for (final StackTraceElement ste : trace) {
                    sb.append("    at ").append(ste).append('\n');
                }
                sb.append('\n');
                if (System.nanoTime() > deadline) {
                    sb.append("...（达到转储时间上限 ").append(DEFAULT_DUMP_BUDGET.toMillis()).append(" ms，提前截断）\n");
                    break;
                }
            }
        } catch (final Throwable ex) {
            sb.append("（线程转储失败：").append(ex).append("）\n");
        }
        return sb.toString();
    }
}
