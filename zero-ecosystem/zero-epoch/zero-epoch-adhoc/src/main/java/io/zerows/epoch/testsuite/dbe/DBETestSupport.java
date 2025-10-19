package io.zerows.epoch.testsuite.dbe;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.vertx.junit5.AppIoTestSupport;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.database.jooq.operation.ADB;
import io.zerows.epoch.store.jooq.DB;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DBE Engine 测试专用基类，用来测试 {@link DB} 类相关功能
 * <p>
 * 🔄 单例模式：整个测试类只启动一次 Vertx 和 ZeroLauncher
 *
 * @author lang : 2025-10-19
 */
@Slf4j
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DBETestSupport extends AppIoTestSupport {

    private static ZeroLauncher<Vertx> container;
    private static Vertx vertx;
    private static final AtomicBoolean containerStarted = new AtomicBoolean(false);
    private static final CountDownLatch startupLatch = new CountDownLatch(1);

    /**
     * 🏗️ 全局初始化：整个测试类只执行一次
     */
    @BeforeAll
    void initializeContainer(final VertxTestContext context) throws InterruptedException {
        if (!containerStarted.get()) {
            synchronized (DBETestSupport.class) {
                if (!containerStarted.get()) {
                    log.info("🚀 Starting ZeroLauncher...");

                    // 创建启动类实例
                    final Class<?> startupClass = this.getStartupClass();
                    if (startupClass == null) {
                        context.failNow(new IllegalArgumentException("Startup class cannot be null"));
                        return;
                    }

                    container = ZeroLauncher.create(startupClass, null);
                    container.start(((vertxInstance, config) -> {
                        vertx = vertxInstance;
                        containerStarted.set(true);
                        startupLatch.countDown(); // 🔓 释放等待的测试
                        log.info("✅ ZeroLauncher started successfully");
                        context.completeNow();
                    }));

                    // 等待启动完成
                    if (!startupLatch.await(30, TimeUnit.SECONDS)) {
                        log.error("❌ ZeroLauncher startup timeout after 30 seconds");
                        context.failNow(new RuntimeException("Container startup timeout"));
                    }
                } else {
                    context.completeNow();
                }
            }
        } else {
            context.completeNow();
        }
    }

    /**
     * 🔍 获取启动类，子类必须重写此方法
     *
     * @return 启动类
     */
    protected abstract Class<?> getStartupClass();

    public ADB db(final Class<?> daoCls) {
        return DB.on(daoCls);
    }

    /**
     * 🔄 等待容器完全启动后执行测试逻辑
     */
    protected void waitForContainerReady(final VertxTestContext context) {
        if (containerStarted.get()) {
            context.completeNow();
        } else {
            try {
                if (startupLatch.await(30, TimeUnit.SECONDS)) {
                    context.completeNow();
                } else {
                    context.failNow(new RuntimeException("Container startup timeout"));
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                context.failNow(new RuntimeException("Wait interrupted", e));
            }
        }
    }

    /**
     * 🔄 获取当前的 Vertx 实例（确保容器已启动）
     */
    protected Vertx getCurrentVertx() {
        if (!containerStarted.get()) {
            throw new IllegalStateException("Container not started yet. Use VertxTestContext to wait for startup.");
        }
        return vertx;
    }

    /**
     * 🔄 获取启动的容器（确保容器已启动）
     */
    protected ZeroLauncher<Vertx> getContainer() {
        if (!containerStarted.get()) {
            throw new IllegalStateException("Container not started yet. Use VertxTestContext to wait for startup.");
        }
        return container;
    }

    /**
     * 🔄 等待启动（使用全局已启动的容器）
     */
    protected void waitStarted(final Class<?> upClass,
                               final VertxTestContext context,
                               final Actuator consumer) {
        // 确保容器已启动
        if (containerStarted.get()) {
            Fn.jvmAt(consumer);
            context.completeNow();
        } else {
            // 等待容器启动完成
            try {
                if (startupLatch.await(30, TimeUnit.SECONDS)) {
                    Fn.jvmAt(consumer);
                    context.completeNow();
                } else {
                    context.failNow(new RuntimeException("Container startup timeout"));
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                context.failNow(new RuntimeException("Wait interrupted", e));
            }
        }
    }
}