package io.zerows.program;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.zerows.cortex.metadata.CodecEnvelop;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
class VertxNative {

    private static Vertx VERTX_NATIVE;

    private static synchronized Vertx nativeRef() {
        synchronized (VertxNative.class) {
            if (Objects.isNull(VERTX_NATIVE)) {
                final VertxOptions options = new VertxOptions();
                options.setMaxEventLoopExecuteTime(3000_000_000_000L);
                options.setMaxWorkerExecuteTime(3000_000_000_000L);
                options.setBlockedThreadCheckInterval(10000);
                options.setPreferNativeTransport(true);
                VERTX_NATIVE = Vertx.vertx(options);
                final EventBus eventBus = VERTX_NATIVE.eventBus();
                eventBus.registerDefaultCodec(Envelop.class, Ut.singleton(CodecEnvelop.class));
            }
            return VERTX_NATIVE;
        }
    }

    static Vertx nativeVertx() {
        return nativeRef();
    }

    static WorkerExecutor nativeWorker(final String name, final Vertx vertx, final Integer minutes) {
        return vertx.createSharedWorkerExecutor(name, 2, minutes, TimeUnit.MINUTES);
    }

    static <T> io.vertx.core.Future<T> nativeWorker(final String name, final Vertx vertx, final Handler<Promise<T>> handler) {
        final WorkerExecutor executor = nativeWorker(name, vertx, 10);

        // 将旧的 Handler<Promise<T>> 包装成 Callable<T>
        return executor.<T>executeBlocking(() -> {
            final Promise<T> promise = Promise.promise();
            try {
                // 执行业务逻辑
                handler.handle(promise);
            } catch (final Throwable ex) {
                promise.fail(ex);
            }
            // 等待 promise 完成，并返回结果（阻塞）
            return promise.future()
                .toCompletionStage()
                .toCompletableFuture()
                .get();
        }).onComplete(ar -> {
            // 无论成功还是失败，都要关闭 WorkerExecutor
            executor.close();
        }).onFailure(error -> {
            // 异常时打印堆栈
            log.error(error.getMessage(), error);
            // 继续往上
        });
    }
}
