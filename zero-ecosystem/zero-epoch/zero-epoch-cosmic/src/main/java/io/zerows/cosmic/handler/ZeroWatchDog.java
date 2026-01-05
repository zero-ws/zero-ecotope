package io.zerows.cosmic.handler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 异步看门狗工具类。
 * 提供超时保护、线程转储分析以及自动重试机制。
 * <p>
 * 修正记录：
 * - 修复了“超时”与“失败”同时发生时导致双重重试的并发 Bug。
 * - 增加了对“僵尸任务”（超时后才完成的任务）的检测日志。
 */
@Slf4j
public final class ZeroWatchDog {

    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final long DEFAULT_BACKOFF_MS = 300L;
    private static final Duration DEFAULT_DUMP_BUDGET = Duration.ofMillis(800);
    /* ===== 默认参数 ===== */
    private static long DEFAULT_TIMEOUT_MS = 20_000L; // io.zerows.platform.EnvironmentVariable.Z_DOG

    static {
        // 优先读取环境变量 Z_DOG，默认为 20000ms
        long timeout = 20_000L;
        try {
            final String env = System.getenv("Z_DOG");
            if (env != null && !env.isBlank()) {
                timeout = Long.parseLong(env.trim());
                log.info("[ ZERO ] ( WatchDog ) 检测到环境变量 Z_DOG，超时调整为 {} ms", timeout);
            }
        } catch (final Exception e) {
            log.warn("[ ZERO ] ( WatchDog ) 环境变量 Z_DOG 解析失败，使用默认值 20000 ms");
        }
        DEFAULT_TIMEOUT_MS = timeout;
    }

    private ZeroWatchDog() {
    }

    /**
     * 为 Future 增加超时保护（默认 5s）。
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
        final Throwable originStack = new Throwable("[ ZERO ] 异步调用发起位置 -> " + name);
        final long startNs = System.nanoTime();

        // 原子锁：确保超时和完成只处理一次
        final AtomicBoolean finished = new AtomicBoolean(false);

        final long timerId = vertx.setTimer(DEFAULT_TIMEOUT_MS, tid -> {
            if (!finished.compareAndSet(false, true)) {
                return; // 已经完成了，忽略超时
            }

            final long elapsed = (System.nanoTime() - startNs) / 1_000_000;
            log.error("[ ZERO ] 看门狗超时（>{}ms）-> {}，elapsed={}ms", DEFAULT_TIMEOUT_MS, name, elapsed);
            log.error("[ ZERO ] 发起调用堆栈（origin stack）如下：", originStack);
            // log.error("[ ZERO ] 线程转储开始 >>>>>>>>>>>>\n{}\n[ ZERO ] 线程转储结束 <<<<<<<<<<<<", dumpAllThreads());

            p.tryFail(new IllegalStateException("[ ZERO ] timeout: " + name + ", " + DEFAULT_TIMEOUT_MS + "ms", originStack));
        });

        origin.onComplete(ar -> {
            if (!finished.compareAndSet(false, true)) {
                return; // 已经因超时处理过了，忽略这次结果
            }
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
     * 带重试（默认：总共 3 次，每次 5s，间隔 300ms）。
     * <p>
     * 关键修正：引入 AtomicBoolean 确保单次尝试中，超时与结果返回互斥，
     * 防止出现 retry 链条分叉。
     */
    public static <T> Future<T> watchAsyncRetry(
        final Vertx vertx,
        final Supplier<Future<T>> supplier,
        final String name
    ) {
        Objects.requireNonNull(vertx, "[ ZERO ] Vertx 不能为空");
        Objects.requireNonNull(supplier, "[ ZERO ] supplier 不能为空");

        final Promise<T> p = Promise.promise();
        final Throwable originStack = new Throwable("[ ZERO ] 异步调用发起位置（含重试链）-> " + name);

        class Attempt {
            int n = 0; // 当前尝试次数 0..DEFAULT_MAX_RETRIES

            void go() {
                final String phase = name + "(attempt#" + (this.n + 1) + ")";
                log.debug("[ ZERO ] 看门狗重试包装：开始 {}", phase);

                // 1. 防御性调用 supplier
                final Future<T> one = safeGet(supplier, phase);
                if (one == null) {
                    p.tryFail(new IllegalStateException("[ ZERO ] supplier 返回了 null Future: " + phase));
                    return;
                }

                final long startNs = System.nanoTime();

                // 2. 关键：当前尝试的互斥锁
                final AtomicBoolean currentFinished = new AtomicBoolean(false);

                // 3. 启动超时计时器
                final long timerId = vertx.setTimer(DEFAULT_TIMEOUT_MS, tid -> {
                    // CAS 抢占：如果能置为 true，说明还没完成，处理超时逻辑
                    if (!currentFinished.compareAndSet(false, true)) {
                        return;
                    }

                    final long elapsed = (System.nanoTime() - startNs) / 1_000_000;
                    log.warn("[ ZERO ] ⚠️ 看门狗单次尝试超时（>{}ms）-> {}，elapsed={}ms", DEFAULT_TIMEOUT_MS, phase, elapsed);
                    // 打印堆栈帮助定位死锁/阻塞
                    // log.error("[ ZERO ] Origin Stack:", originStack);

                    // 触发重试逻辑（视为 TimeoutException 失败）
                    this.retryOrFail(new java.util.concurrent.TimeoutException("WatchDog Timeout: " + phase));
                });

                // 4. 监听实际任务完成
                one.onComplete(ar -> {
                    // CAS 抢占：如果能置为 true，说明没超时，正常处理结果
                    if (!currentFinished.compareAndSet(false, true)) {
                        // 抢占失败：说明定时器先触发了超时。
                        // 此时这个任务变成了“僵尸任务”。
                        if (ar.succeeded()) {
                            log.warn("[ ZERO ] 👻 僵尸任务成功返回（但已被超时机制放弃）-> {}", phase);
                        } else {
                            log.debug("[ ZERO ] 👻 僵尸任务失败返回 -> {}", phase);
                        }
                        return;
                    }

                    // 抢占成功：取消定时器
                    vertx.cancelTimer(timerId);

                    if (ar.succeeded()) {
                        p.tryComplete(ar.result());
                    } else {
                        log.warn("[ ZERO ] 看门狗：{} 执行失败，原因={}", phase, ar.cause().getMessage());
                        this.retryOrFail(ar.cause());
                    }
                });
            }

            // 统一的重试决策逻辑
            void retryOrFail(final Throwable cause) {
                if (this.n < DEFAULT_MAX_RETRIES) {
                    this.n++;
                    log.info("[ ZERO ] 🔄 准备第 {}/{} 次重试，等待 {}ms...",
                        this.n + 1, DEFAULT_MAX_RETRIES + 1, DEFAULT_BACKOFF_MS);

                    vertx.setTimer(DEFAULT_BACKOFF_MS, id -> this.go());
                } else {
                    log.error("[ ZERO ] ❌ 最终失败（已重试 {} 次）-> {}", this.n, name);
                    p.tryFail(new IllegalStateException("[ ZERO ] Final Failure: " + name, cause));
                }
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
            log.error("[ ZERO ] supplier.get() 抛出同步异常 -> {}，异常={}", phase, e.toString());
            return Future.failedFuture(e);
        }
    }

    /**
     * 收集所有线程栈（带时间预算，防刷屏）。
     */
    private static String dumpAllThreads() {
        final long deadline = System.nanoTime() + DEFAULT_DUMP_BUDGET.toNanos();
        final StringBuilder sb = new StringBuilder(8192);
        try {
            for (final Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
                final Thread t = e.getKey();
                final StackTraceElement[] trace = e.getValue();
                sb.append("Thread[").append(t.getName())
                    .append("] id=").append(t.threadId())
                    .append(" state=").append(t.getState())
                    .append('\n');
                for (final StackTraceElement ste : trace) {
                    sb.append("    at ").append(ste).append('\n');
                }
                sb.append('\n');
                if (System.nanoTime() > deadline) {
                    sb.append("... (Truncated due to dump budget limit)\n");
                    break;
                }
            }
        } catch (final Throwable ex) {
            sb.append("(Dump Failed: ").append(ex).append(")\n");
        }
        return sb.toString();
    }
}