package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.exception._80254Exception401LoginRetry;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-10-29
 */
@Slf4j
class BackendProviderLogged extends BackendProviderBase {


    BackendProviderLogged(final Vertx vertxRef, final SecurityMeta meta) {
        super(vertxRef, meta);
    }

    @Override
    public boolean support(final TokenType type) {
        return TokenType.BASIC != type;
    }

    @Override
    protected Future<User> authenticate(final JsonObject credentialsJ, final JsonObject cachedJ) {
        if (Ut.isNil(cachedJ)) {
            return Future.failedFuture(new _80254Exception401LoginRetry());
        }
        // 构造新的用户基础数据
        final User authorized = User.create(credentialsJ);
        return Future.succeededFuture(authorized);
    }
}
