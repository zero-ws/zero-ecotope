package io.zerows.core.database.jooq;

import io.github.jklingsporn.vertx.jooq.classic.VertxDAO;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.zerows.core.database.jooq.exception.BootJooqClassInvalidException;
import io.zerows.core.database.jooq.exception.BootJooqVertxNullException;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;
import org.jooq.Configuration;
import org.jooq.DSLContext;

import java.util.concurrent.Callable;

/**
 * Container to wrap Jooq / VertxDAO
 * 1. Jooq DSL will support sync operation
 * 2. VertxDAO support the async operation
 *
 * The instance counter is as following:
 *
 * 1. 1 vertx instance
 * 2. 1 configuration instance ( 1 context instance )
 * 3. n clazz = Dao instances
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class JooqDsl {
    private static final OLog LOGGER = Ut.Log.database(JooqDsl.class);

    private static final Cc<String, JooqDsl> CC_DSL = Cc.open();
    private transient Vertx vertxRef;
    private transient Configuration configurationRef;
    private transient String poolKey;

    private transient VertxDAO dao;

    private JooqDsl(final String poolKey) {
        this.poolKey = poolKey;
    }

    static JooqDsl init(final Vertx vertxRef, final Configuration configurationRef, final Class<?> daoCls) {
        // Checking when initializing
        Fx.out(!Ut.isImplement(daoCls, VertxDAO.class), BootJooqClassInvalidException.class, JooqDsl.class, daoCls.getName());
        Fx.outBoot(null == vertxRef, LOGGER, BootJooqVertxNullException.class, daoCls);

        // Calculate the key of current pool
        final String poolKey = String.valueOf(vertxRef.hashCode()) + ":" +
            String.valueOf(configurationRef.hashCode()) + ":" + daoCls.getName();
        return CC_DSL.pick(() -> new JooqDsl(poolKey).bind(vertxRef, configurationRef).store(daoCls), poolKey);
        // return Fx.po?l(DSL_MAP, poolKey, () -> new JooqDsl(poolKey).bind(vertxRef, configurationRef).get(daoCls));
    }

    private JooqDsl bind(final Vertx vertxRef, final Configuration configurationRef) {
        this.vertxRef = vertxRef;
        this.configurationRef = configurationRef;
        return this;
    }

    private <T> JooqDsl store(final Class<?> daoCls) {
        /*
         * VertxDao initializing
         * Old:
         * final Tool dao = Ut.instance(clazz, configuration);
         * Ut.invoke(dao, "setVertx", vertxRef);
         */
        final VertxDAO vertxDAO = Ut.instance(daoCls, configurationRef, vertxRef);
        this.dao = vertxDAO;
        return this;
    }

    public String poolKey() {
        return this.poolKey;
    }

    // ----------------------- KMetadata

    public DSLContext context() {
        return this.configurationRef.dsl();
    }

    public VertxDAO dao() {
        return this.dao;
    }

    public <T> Future<T> executeBlocking(Handler<Promise<T>> blockingCodeHandler) {
        Promise<T> promise = Promise.promise();

        // 关键：用 Callable 包裹 blockingCodeHandler
        Callable<T> callable = () -> {
            try {
                blockingCodeHandler.handle(promise);
                // 这里不能直接返回结果，因为用户是异步 complete()
                // 所以返回 null 交给 promise.future() 处理
                return null;
            } catch (Throwable e) {
                promise.fail(e);
                return null;
            }
        };


        // 调用 Vert.x 5.x 的 API
        vertxRef.executeBlocking(callable, false)
            .onComplete(ar -> {
                // 如果用户在 handler 里 complete/fail 了，这里就不用管
                // 如果用户什么都没做，就把 Vert.x 的结果传回
                if (ar.succeeded() && !promise.future().isComplete()) {
                    promise.complete(ar.result());
                } else if (ar.failed() && !promise.future().isComplete()) {
                    promise.fail(ar.cause());
                }
            });

        return promise.future();
    }
}
