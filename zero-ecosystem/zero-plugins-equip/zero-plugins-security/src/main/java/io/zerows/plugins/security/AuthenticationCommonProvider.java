package io.zerows.plugins.security;

import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.cache.Rapid;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-29
 */
@Slf4j
class AuthenticationCommonProvider implements AuthenticationProvider {

    private final SecurityMeta meta;
    private final Vertx vertxRef;

    AuthenticationCommonProvider(final Vertx vertxRef, final SecurityMeta meta) {
        this.meta = meta;
        this.vertxRef = vertxRef;
    }

    /**
     * 此处的 {@link Credentials} 只可能是两种类型
     * <pre>
     *     1. {@link UsernamePasswordCredentials}
     *        {
     *            "username": "???",
     *            "password": "???"
     *        }
     *     2. {@link TokenCredentials}
     *        {
     *            "token": "???",
     *            "scope": "???"
     *        }
     * </pre>
     * 针对不同的类型，底层调用不同的认证逻辑来处理
     */
    @Override
    public Future<User> authenticate(final Credentials credentials) {
        // 提取不同的认证信息
        final JsonObject authJson = this.valueCredentials(credentials);
        // 提取缓存信息
        final String session = Ut.valueString(authJson, KName.SESSION);
        if (Ut.isNil(session)) {
            return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 缺失认证会话信息！"));
        }
        // 提取会话专用缓存
        final Rapid<String, JsonObject> cached = Rapid.object(session);
        return cached.read(KWeb.CACHE.User.AUTHENTICATE).compose(res -> {
            if (Ut.isNotNil(res)) {
                // 缓存中有值，可直接返回
                log.info("[ PLUG ] ( Secure ) 401 用户认证命中缓存，session = {}", session);
                return Future.succeededFuture(User.create(authJson));
            }


            // 缓存中没有值，要重新认证
            return this.userVerify(authJson).compose(verified -> {
                if (!verified) {
                    log.error("[ PLUG ] ( Secure ) 401 用户认证失败，session = {}", session);
                    return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 用户认证失败！"));
                }


                // 认证成功，写入缓存
                log.info("[ PLUG ] ( Secure ) 401 用户认证成功，写入缓存，session = {}", session);
                return cached.write(KWeb.CACHE.User.AUTHENTICATE, authJson)
                        .compose(ignored -> Future.succeededFuture(User.create(authJson)));
            });
        });
    }


    private Future<Boolean> userVerify(final JsonObject authJson) {
        final WallExecutor executor = this.meta.getProxy();
        if (Objects.isNull(executor)) {
            return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 认证执行器未找到！"));
        }
        return executor.authenticate(authJson).compose(res -> {
            if (Objects.isNull(res)) {
                return Future.succeededFuture(Boolean.FALSE);
            }
            return Future.succeededFuture(res);
        });
    }

    private JsonObject valueCredentials(final Credentials credentials) {
        final JsonObject authJson = credentials.toJson();
        if (authJson.containsKey(KName.USERNAME)) {
            // username -> session
            authJson.put(KName.SESSION, authJson.getString(KName.USERNAME));
        }
        if (authJson.containsKey(KName.TOKEN)) {
            // token -> session
            authJson.put(KName.SESSION, authJson.getString(KName.TOKEN));
            // token -> access_token
            authJson.put(KName.ACCESS_TOKEN, authJson.getString(KName.TOKEN));
        }
        return authJson;
    }
}
