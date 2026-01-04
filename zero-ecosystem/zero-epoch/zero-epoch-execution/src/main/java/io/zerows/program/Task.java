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
     * 适用于外层需要组合 (Compose) 或不需要立即结果的场景。
     * </p>
     *
     * @param executor 具体的业务逻辑（通常是 CPU 密集型或阻塞型代码）
     * @param <T>      返回值类型
     * @return 异步 Future
     */
    static <T> Future<T> async(final Supplier<T> executor) {
        return vertx().executeBlocking(() -> {
            try {
                return executor.get();
            } catch (final Exception e) {
                // 确保异常被捕获并传递给 Future
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 2. 同步模式 (Sync)
     * <p>
     * 将任务调度到 Vert.x Worker 线程池执行，并阻塞（挂起）等待结果。
     * <p>
     * 智能切换逻辑：
     * - 如果当前是虚拟线程：使用 Future.await() (高性能挂起)
     * - 如果当前是普通线程：使用 CompletableFuture.get() (兼容性阻塞)
     * - 如果当前是 EventLoop：抛出异常 (防止死锁)
     * </p>
     *
     * @param executor 具体的业务逻辑
     * @param <T>      返回值类型
     * @return 执行结果
     */
    static <T> T sync(final Supplier<T> executor) {
        // 1. 先调度去 Worker 执行
        final Future<T> future = async(executor);

        // 2. 智能等待结果
        return smartAwait(future);
    }

    // =========================================================================
    // 私有内核方法
    // =========================================================================

    /**
     * 智能等待策略
     */
    private static <T> T smartAwait(final Future<T> future) {
        // A. 死锁防御
        if (Context.isOnEventLoopThread()) {
            throw new IllegalStateException("[ Zero ] 严禁在 EventLoop 线程中调用同步等待(sync)，请改用异步模式(async)！");
        }

        // B. 现代模式 (Virtual Thread)
        // 利用 Java 21+ 特性判断
        if (Thread.currentThread().isVirtual()) {
            return Future.await(future);
        }

        // C. 兼容模式 (Platform Thread / Worker)
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
            // 剥离 ExecutionException，抛出真实的业务异常
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