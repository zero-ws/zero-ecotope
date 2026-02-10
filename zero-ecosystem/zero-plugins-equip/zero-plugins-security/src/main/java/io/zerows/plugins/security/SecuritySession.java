package io.zerows.plugins.security;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.UserContextInternal;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.cache.HMM;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.support.Ut;

import java.time.Duration;
import java.util.Objects;

public class SecuritySession {
    private static final Cc<String, SecuritySession> CC_AUTH_SESSION = Cc.openThread();
    private static YmSecurity SECURITY;

    private SecuritySession() {
    }

    public static SecuritySession of() {
        return CC_AUTH_SESSION.pick(SecuritySession::new);
    }

    private YmSecurity security() {
        if (Objects.isNull(SECURITY)) {
            SECURITY = SecurityActor.configuration();
        }
        return SECURITY;
    }

    public boolean isDisabled403() {
        return !this.security().isAuthorization();
    }

    public boolean isAnonymous(final RoutingContext context) {
        final String authorization = context.request().getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isEmpty(authorization)) {
            return true;
        }
        final TokenType type = TokenType.fromString(authorization);
        return TokenType.BASIC == type;
    }

    public long msExpiredAt() {
        final String msExpired = this.security().getLimit().getExpiredAt();
        final Duration expiredAt = R2MO.toDuration(msExpired);
        return expiredAt.toSeconds();
    }

    /**
     * 此方法有三个位置会被调用
     * <pre>
     *     1. 401 过程中两处
     *        - 带 Token 的认证处理
     *        - 匿名模式的认证处理
     *     2. 403 过程中执行（authorization -> true），启用授权功能后
     * </pre>
     *
     * @param context   RoutingContext 对象，包含了当前请求的上下文信息
     * @param recovered 从缓存中恢复的 User 对象，可能为 null
     * @return 合法的 User 对象
     */
    User authorizedUser(final RoutingContext context, final User recovered) {
        if (Objects.isNull(recovered)) {
            return null;
        }
        ((UserContextInternal) context.userContext()).setUser(recovered);
        return recovered;
    }

    public Future<JsonObject> authorized403(final RoutingContext context, final JsonObject waitFor) {
        return this.authorized(context)
            .put(KWeb.SESSION.AUTHORIZATION, waitFor, this.msExpiredAt());
    }

    public HMM<String, JsonObject> authorized(final RoutingContext context) {
        final String authorization = context.request().getHeader(HttpHeaders.AUTHORIZATION);
        final String token = authorization.split(" ")[1];
        return HMM.of(token);
    }

    public Future<UserAt> authorized401(final UserAt userAt, final String token) {
        // 处理临时用户
        final JsonObject userData = Account.userData(userAt);
        final String ephemeral = Ut.valueString(userData, KName.ID);
        if (Objects.isNull(ephemeral)) {
            return Future.succeededFuture(userAt);
        }
        /*
         * 加载 Token 的过期时间，Token 过期时间和用户缓存存在时间是一致的，所以此处要直接从配置中提取
         * 并且写入到缓存中去！单位内置为秒
         */
        final HMM<String, JsonObject> mm = HMM.of(token);
        return mm.put(KWeb.SESSION.AUTHENTICATE, userData, this.msExpiredAt())
            .compose(stored -> UserSession.of().userAt(userAt).compose());
    }
}
