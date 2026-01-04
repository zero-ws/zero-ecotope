package io.zerows.program;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.cortex.management.StoreVertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * 任务调度内核 (Package-Private)
 * <p>
 * 该类仅对 io.zerows.program 包内的工具类可见。
 * 负责底层的线程环境探测、Worker 调度以及同步/异步模式切换。
 * </p>
 */
@Slf4j
class Task {

    private static final StoreVertx store = StoreVertx.of();
    private static final long LEGACY_TIMEOUT_SEC = 60;

    /**
     * 获取 Vertx 实例，若未初始化则抛出异常
     */
    private static Vertx vertx() {
        final Vertx vertx = store.vertx();
        if (Objects.isNull(vertx)) {
            throw new IllegalStateException("[ Zero ] Vertx 实例未初始化，无法调度任务！");
        }
        return vertx;
    }

    // =========================================================================
    // 开放给同包工具类的两个核心方法
    // =========================================================================

    /**
     * 1. 异步模式 (Async)
     * <p>
     * 将任务调度到 Vert.x Worker 线程池执行，并立即返回 Future。
     * </p>
     */
    static <T> Future<T> async(final Supplier<T> executor) {
        return vertx().executeBlocking(() -> {
            try {
                return executor.get();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 2. 同步模式 (Sync)
     * <p>
     * 调度到 Worker 并等待结果。
     * </p>
     */
    static <T> T sync(final Supplier<T> executor) {
        final Future<T> future = async(executor);
        return smartAwait(future);
    }

    // =========================================================================
    // 私有内核方法
    // =========================================================================

    /**
     * 智能等待策略 (增加 Worker 模式探测)
     */
    private static <T> T smartAwait(final Future<T> future) {
        // A. 【死锁防御】严禁 EventLoop
        if (Context.isOnEventLoopThread()) {
            throw new IllegalStateException("[ Zero ] 严禁在 EventLoop 线程中调用同步等待(sync)，请改用异步模式(async)！");
        }

        // B. 【现代模式】虚拟线程 (Virtual Thread)
        // 无论是 Vert.x 管理的虚拟 Worker 还是外部虚拟线程，只要是 Virtual，就由 JVM 调度挂起
        if (Thread.currentThread().isVirtual()) {
            return Future.await(future);
        }

        // C. 【Worker 防护】检测是否为 Vert.x 的普通 Worker 线程 (Platform Worker)
        // 代码运行到这里，说明 isVirtual() 为 false，即它是物理线程。
        // 如果它同时是 Context.isOnWorkerThread()，说明这是 Vert.x 传统的 Worker 池。
        if (Context.isOnWorkerThread()) {
            log.trace("[ Zero ] 检测到普通 Worker 线程，降级使用 JDK 阻塞等待。");
            return legacyWait(future);
        }

        // D. 【兼容模式】其他外部线程 (main, 第三方线程池等)
        return legacyWait(future);
    }

    /**
     * 传统 JDK 阻塞等待
     */
    private static <T> T legacyWait(final Future<T> future) {
        try {
            return future.toCompletionStage()
                .toCompletableFuture()
                .get(LEGACY_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("[ Zero ] 任务被中断", e);
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(cause);
        } catch (final TimeoutException e) {
            throw new RuntimeException("[ Zero ] 任务执行超时 (" + LEGACY_TIMEOUT_SEC + "s)", e);
        }
    }
}