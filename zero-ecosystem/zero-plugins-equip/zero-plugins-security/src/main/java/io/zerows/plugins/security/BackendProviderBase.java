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
import io.zerows.plugins.security.service.AsyncSession;
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
        if (!(credentials instanceof final AsyncSession session)) {
            return Future.failedFuture(new _80250Exception401SessionNull(credentials.toJson().encode()));
        }
        final JsonObject credentialJ = credentials.toJson();
        final String sessionId = session.getSessionId();
        if (Ut.isNil(sessionId)) {
            return Future.failedFuture(new _80250Exception401SessionNull(credentialJ.encode()));
        }

        final JsonObject authJ = new JsonObject();
        authJ.put(KName.SESSION, session);
        authJ.mergeIn(credentialJ, true);
        // 已经包含了会话基本信息，所以此处可直接提取
        final String token = credentials.toHttpAuthorization().split(" ")[1];
        final HMM<String, JsonObject> mmSession = HMM.of(token);
        this.log().info("[ PLUG ] ( Secure ) 401 cache lookup: provider = {}, session = {}, cache = {}, credential.id = {}, credential.session = {}, credential.subject = {}, credential.habitus = {}",
            this.getClass().getName(), sessionId, mmSession.name(),
            Ut.valueString(credentialJ, KName.ID),
            Ut.valueString(credentialJ, KName.SESSION),
            Ut.valueString(credentialJ, KName.SUBJECT),
            Ut.valueString(credentialJ, KName.HABITUS));
        return mmSession.find(KWeb.SESSION.AUTHENTICATE)
            .onSuccess(cached -> {
                if (Ut.isNil(cached)) {
                    this.log().warn("[ PLUG ] ( Secure ) 401 cache miss: provider = {}, session = {}, cache = {}, key = {}",
                        this.getClass().getName(), sessionId, mmSession.name(), KWeb.SESSION.AUTHENTICATE);
                } else {
                    this.log().info("[ PLUG ] ( Secure ) 401 cache hit: provider = {}, session = {}, cache = {}, user.id = {}, user.session = {}, user.username = {}",
                        this.getClass().getName(), sessionId, mmSession.name(),
                        Ut.valueString(cached, KName.ID),
                        Ut.valueString(cached, KName.SESSION),
                        Ut.valueString(cached, KName.USERNAME));
                }
            })
            .compose(cached -> this.authenticate(authJ, cached))
            .compose(authorized -> {
                if (Objects.isNull(authorized)) {
                    return Future.failedFuture(new _80251Exception401UserInvalid());
                } else {
                    return Future.succeededFuture(authorized);
                }
            })
            .onFailure(cause -> this.log().warn("[ PLUG ] ( Secure ) backend provider failed: provider = {}, session = {}, cause = {}: {}",
                this.getClass().getName(), sessionId, cause.getClass().getName(), cause.getMessage()));
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected abstract Future<User> authenticate(final JsonObject credentialsJ, final JsonObject cachedJ);
}
