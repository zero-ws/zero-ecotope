package io.zerows.program;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.zerows.cortex.management.StoreVertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
class WaitTask {

    private static final StoreVertx store = StoreVertx.of();
    private static final long LEGACY_TIMEOUT_SEC = 60;

    private static Vertx vertx() {
        final Vertx vertx = store.vertx();
        if (Objects.isNull(vertx)) {
            throw new IllegalStateException("[ Zero ] Vertx 实例未初始化，无法调度任务！");
        }
        return vertx;
    }

    /**
     * 1. 物理异步模式 (Async - Platform Worker)
     * <p>
     * 适用于 CPU 密集型任务（如图片生成、加密计算）。
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
     * 2. 虚拟异步模式 (Async - Virtual Thread) 🚀 新增
     * <p>
     * 适用于 I/O 密集型任务，或者必须使用 Future.await() 的遗留代码。
     * 直接启动 Java 21 虚拟线程，不占用 Vert.x 的物理 Worker 线程池。
     * </p>
     */
    static <T> Future<T> asyncVirtual(final Supplier<T> executor) {
        final Promise<T> promise = Promise.promise();
        // 手动启动虚拟线程
        Thread.ofVirtual().name("zero-vt-task").start(() -> {
            try {
                // 在虚拟线程中，调用 Future.await() 是合法的
                final T result = executor.get();
                if (Objects.isNull(result)) {
                    promise.complete();
                } else {
                    promise.complete(result);
                }
            } catch (final Throwable e) {
                log.error(e.getMessage(), e);
                promise.fail(e);
            }
        });
        return promise.future();
    }

    /**
     * 3. 同步模式 (Sync)
     */
    static <T> T sync(final Supplier<T> executor) {
        if (Context.isOnEventLoopThread()) {
            throw new IllegalStateException("[ Zero ] 严禁在 EventLoop 线程中调用同步等待(sync)！");
        }
        final Future<T> future = async(executor);
        return smartAwait(future);
    }

    // ... smartAwait 和 legacyWait 保持不变 ...

    private static <T> T smartAwait(final Future<T> future) {
        if (Thread.currentThread().isVirtual()) {
            return Future.await(future);
        }
        if (Context.isOnWorkerThread()) {
            log.warn("[ Zero ] ⚠️ Worker 线程同步阻塞警告！");
        }
        return legacyWait(future);
    }

    private static <T> T legacyWait(final Future<T> future) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> resultRef = new AtomicReference<>();
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                resultRef.set(ar.result());
            } else {
                errorRef.set(ar.cause());
            }
            latch.countDown();
        });
        try {
            if (!latch.await(LEGACY_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                throw new RuntimeException("[ Zero ] Timeout");
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        final Throwable err = errorRef.get();
        if (err != null) {
            throw new RuntimeException(err);
        }
        return resultRef.get();
    }
}