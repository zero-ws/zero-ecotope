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
import io.zerows.epoch.web.Account;
import io.zerows.plugins.cache.HMM;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-29
 */
@Slf4j
class AuthenticationProviderOne implements AuthenticationProvider {

    private final SecurityMeta meta;
    private final Vertx vertxRef;

    AuthenticationProviderOne(final Vertx vertxRef, final SecurityMeta meta) {
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
        // Fix-AUTH-001 提取不同的认证信息
        final JsonObject authJson = Account.userData(credentials);
        // 提取缓存信息
        final String session = Ut.valueString(authJson, KName.SESSION);
        if (Ut.isNil(session)) {
            return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 缺失认证会话信息！"));
        }
        /*
         * 会话缓存的提取
         * - 加载缓存在认证的时候做
         * - 写入缓存应该在登录接口中执行，简单说登录成功之后写入缓存，而并非在 Provider 验证的时候写入缓存
         * 况且有一个特殊条件：Basic 认证模式下，登录接口并不存在
         */
        final HMM<String, JsonObject> mmSession = HMM.of(session);
        return mmSession.find(KWeb.CACHE.User.AUTHENTICATE).compose(res -> {
            if (Ut.isNotNil(res)) {
                // 缓存中有值，可直接返回
                log.info("[ PLUG ] ( Secure ) 401 用户认证命中缓存，session = {}", session);
                return Future.succeededFuture(User.create(authJson));
            }

            final WallExecutor executor = this.meta.getProxy();
            if (Objects.isNull(executor)) {
                return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 认证执行器未找到！"));
            }
            // 缓存中没有值，要重新认证
            return executor.authenticate(authJson).compose(authorized -> {
                if (Objects.isNull(authorized)) {
                    log.error("[ PLUG ] ( Secure ) 401 用户认证失败，session = {}", session);
                    return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 用户认证失败！"));
                }
                return Future.succeededFuture(authorized);
            });
        });
    }
}
