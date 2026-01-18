package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.cache.HMM;
import io.zerows.plugins.security.exception._80250Exception401SessionNull;
import io.zerows.plugins.security.exception._80251Exception401UserInvalid;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class BackendProviderBase implements BackendProvider {
    private final SecurityMeta meta;
    private final Vertx vertxRef;

    BackendProviderBase(final Vertx vertxRef, final SecurityMeta meta) {
        this.meta = meta;
        this.vertxRef = vertxRef;
    }

    protected Vertx vertx() {
        return this.vertxRef;
    }

    protected SecurityMeta meta() {
        return this.meta;
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        final String session = Vertx.currentContext().get(SecurityConstant.KEY_SESSION);
        final JsonObject credentialJ = credentials.toJson();
        if (Ut.isNil(session)) {
            return Future.failedFuture(new _80250Exception401SessionNull(credentialJ.encode()));
        }

        final JsonObject authJ = new JsonObject();
        authJ.put(KName.SESSION, session);
        authJ.mergeIn(credentialJ, true);
        // 已经包含了会话基本信息，所以此处可直接提取
        final HMM<String, JsonObject> mmSession = HMM.of(session);
        return mmSession.find(KWeb.CACHE.User.AUTHENTICATE)
            .compose(cached -> this.authenticate(authJ, cached))
            .compose(authorized -> {
                if (Objects.isNull(authorized)) {
                    return Future.failedFuture(new _80251Exception401UserInvalid());
                } else {
                    return Future.succeededFuture(authorized);
                }
            });
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected abstract Future<User> authenticate(final JsonObject credentialsJ, final JsonObject cachedJ);
}
