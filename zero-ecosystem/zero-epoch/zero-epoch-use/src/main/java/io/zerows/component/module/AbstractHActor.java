package io.zerows.component.module;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lang : 2025-10-13
 */
public abstract class AbstractHActor implements HActor {
    @Override
    public <T> Future<Boolean> startAsync(final HConfig config, final T containerRef) {
        if (containerRef instanceof final Vertx vertxRef) {
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            return vertxRef.executeBlocking(() -> {
                    // 1) 先在 worker 线程里打印一遍
                    logger.info("[ ZMOD ]  \t🐦‍🔥 ---> 运行 actor = `{}` / hash = {} | thread={}",
                        this.getClass().getName(), this.hashCode(), Thread.currentThread().getName());
                    return true; // Callable 必须返回一个值，这里随便给 true
                })
                // 2) 然后继续你原来的异步逻辑（回到 Vert.x Future 链）
                .compose(ignored -> {
                    final Future<Boolean> executed = this.startAsync(config, vertxRef);
                    if (executed == null) {
                        logger.warn("[ ZMOD ]  \t\t❗️ ---> Actor = `{}` 执行失败，返回值为 null！", this.getClass().getName());
                        return Future.succeededFuture(false);
                    }
                    return executed;
                })
                .recover(e -> {
                    logger.error("[ ZMOD ]  \t\t❗️ ---> Actor = `{}` 执行异常", this.getClass().getName(), e);
                    return Future.failedFuture(e);
                });
        }
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected void vLog(final String message, final Object... params) {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("[ ZMOD ]  \t\t \uD83D\uDCA4 ---> " + message, params);
    }

    protected abstract Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef);
}
