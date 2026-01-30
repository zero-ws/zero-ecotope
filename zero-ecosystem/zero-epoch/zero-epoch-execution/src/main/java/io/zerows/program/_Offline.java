package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

/**
 * @author lang : 2023-06-11
 */
class _Offline extends _Get {
    // ---------------------- Agent mode usage --------------------------

    public static Vertx nativeVertx() {
        return VertxNative.nativeVertx();
    }

    public static WorkerExecutor nativeWorker(final String name) {
        return VertxNative.nativeWorker(name, nativeVertx(), 10);
    }

    public static WorkerExecutor nativeWorker(final String name, final Vertx vertx) {
        return VertxNative.nativeWorker(name, vertx, 10);
    }

    public static WorkerExecutor nativeWorker(final String name, final Integer mins) {
        return VertxNative.nativeWorker(name, nativeVertx(), mins);
    }

    public static WorkerExecutor nativeWorker(final String name, final Vertx vertx, final Integer mins) {
        return VertxNative.nativeWorker(name, vertx, mins);
    }

    public static <T> Future<T> nativeWorker(final String name, final Handler<Promise<T>> handler) {
        return VertxNative.nativeWorker(name, nativeVertx(), handler);
    }

    public static <T> Future<T> nativeWorker(final String name, final Vertx vertx, final Handler<Promise<T>> handler) {
        return VertxNative.nativeWorker(name, vertx, handler);
    }
}
