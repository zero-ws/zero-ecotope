package io.zerows.plugins.weco;

import io.r2mo.xync.weco.WeCoSession;
import io.vertx.core.Future;
import io.zerows.program.Ux;

import java.time.Duration;

public interface WeCoAsyncSession extends WeCoSession {

    default Future<Boolean> saveAsync(final String cacheKey, final String statusOr, final Duration expiredAt) {
        return Ux.waitVirtual(() -> {
            this.save(cacheKey, statusOr, expiredAt);
            return true;
        });
    }

    default Future<String> getAsync(final String cacheKey, final Duration expiredAt) {
        return Ux.waitVirtual(() -> this.get(cacheKey, expiredAt));
    }
}
