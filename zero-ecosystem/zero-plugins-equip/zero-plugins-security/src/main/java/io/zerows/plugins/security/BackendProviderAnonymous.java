package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.exception._80252Exception401CacheConfuse;
import io.zerows.plugins.security.exception._80253Exception404WallExecutor;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class BackendProviderAnonymous extends BackendProviderBase {
    BackendProviderAnonymous(final Vertx vertxRef, final SecurityMeta meta) {
        super(vertxRef, meta);
    }

    @Override
    public boolean support(final TokenType type) {
        return TokenType.BASIC == type;
    }

    @Override
    protected Future<User> authenticate(final JsonObject credentialsJ, final JsonObject cachedJ) {
        if (Ut.isNotNil(cachedJ)) {
            final String session = Ut.valueString(credentialsJ, KName.SESSION);
            return Future.failedFuture(new _80252Exception401CacheConfuse(session, KWeb.SESSION.AUTHENTICATE));
        }


        final WallExecutor executor = this.meta().getProxy();
        if (Objects.isNull(executor)) {
            return Future.failedFuture(new _80253Exception404WallExecutor(this.meta().getType()));
        }
        return executor.authenticate(credentialsJ);
    }
}
