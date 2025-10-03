package io.zerows.program;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.zerows.component.codec.EnvelopCodec;
import io.zerows.component.transformer.TransformerVertx;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

class VertxNative {

    private static Vertx VERTX_NATIVE;

    private static synchronized Vertx nativeRef() {
        synchronized (VertxNative.class) {
            if (Objects.isNull(VERTX_NATIVE)) {
                VERTX_NATIVE = Vertx.vertx(TransformerVertx.nativeOption());
                final EventBus eventBus = VERTX_NATIVE.eventBus();
                eventBus.registerDefaultCodec(Envelop.class, Ut.singleton(EnvelopCodec.class));
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

            if (ar.failed()) {
                // 异常时打印堆栈
                final Throwable error = ar.cause();
                if (!(error instanceof VertxException)) {
                    error.printStackTrace();
                }
            }
        });
    }
}
