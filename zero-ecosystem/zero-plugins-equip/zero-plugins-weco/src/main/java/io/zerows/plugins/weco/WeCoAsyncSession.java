package io.zerows.plugins.weco;

import io.r2mo.typed.cc.Cc;
import io.r2mo.xync.weco.WeCoSession;
import io.vertx.core.Future;
import io.zerows.spi.HPI;

import java.time.Duration;

public interface WeCoAsyncSession extends WeCoSession {

    Cc<String, WeCoAsyncSession> CC_ASYNC_SESSION = Cc.openThread();

    static WeCoAsyncSession of() {
        return CC_ASYNC_SESSION.pick(() -> HPI.findOneOf(WeCoAsyncSession.class));
    }

    default Future<Boolean> saveAsync(final String cacheKey, final String statusOr, final Duration expiredAt) {
        this.save(cacheKey, statusOr, expiredAt);
        return Future.succeededFuture(Boolean.TRUE);
    }

    default Future<String> getAsync(final String cacheKey, final Duration expiredAt) {
        return Future.succeededFuture(this.get(cacheKey, expiredAt));
    }
}
