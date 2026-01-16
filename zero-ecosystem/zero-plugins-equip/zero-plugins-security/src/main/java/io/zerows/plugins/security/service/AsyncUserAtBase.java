package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.hashing.HashingStrategy;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.cache.HMM;
import io.zerows.plugins.security.exception._80203Exception404UserNotFound;
import io.zerows.plugins.security.exception._80204Exception401PasswordWrong;
import io.zerows.plugins.security.exception._80244Exception401LoginTypeWrong;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public abstract class AsyncUserAtBase implements AsyncUserAt {
    private final TypeLogin typeLogin;
    private final HashingStrategy strategy;

    protected AsyncUserAtBase(final TypeLogin typeLogin) {
        this.typeLogin = typeLogin;
        this.strategy = HashingStrategy.load();
    }

    @Override
    public Future<UserAt> loadLogged(final LoginRequest request) {
        final TypeLogin typeRequest = request.type();
        if (Objects.isNull(typeRequest) || this.typeLogin != typeRequest) {
            return Fx.failOut(_80244Exception401LoginTypeWrong.class, typeRequest, this.typeLogin);
        }
        // 此处不加载员工数据，员工数据的选择交给 UserContext 来处理
        final String identifier = request.getId();
        log.info("[ ZERO ] 登录加载：id = `{}` / provider = `{}`", identifier, this.getClass().getName());
        return this.findUser(identifier).compose(verified -> {
            if (Objects.isNull(verified)) {
                // 用户找不到
                return Fx.failOut(_80203Exception404UserNotFound.class, identifier);
            }
            return this.isMatched(request, verified).compose(authorized -> {
                if (!authorized) {
                    // 密码不匹配
                    return Fx.failOut(_80204Exception401PasswordWrong.class, identifier);
                }
                return Future.succeededFuture(verified);
            });
        }).compose(userAt -> {
            // 处理临时用户
            final JsonObject userData = Account.userData(userAt);
            final String ephemeral = Ut.valueString(userData, this.userCache());
            if (Objects.isNull(ephemeral)) {
                return Future.succeededFuture(userAt);
            }
            // 构造缓存
            final HMM<String, JsonObject> mm = HMM.of(ephemeral);
            return mm.put(KWeb.CACHE.User.AUTHENTICATE, userData)
                .compose(nil -> Future.succeededFuture(userAt));
        });
    }

    public TypeLogin loginType() {
        return Objects.isNull(this.typeLogin) ? TypeLogin.PASSWORD : this.typeLogin;
    }

    protected String userCache() {
        return KName.TOKEN;
    }

    @Override
    public Future<UserAt> loadLogged(final String identifier) {
        // 缓存中加载账号数据
        final UserAt cached = UserSession.of().find(identifier);
        if (Objects.nonNull(cached) && cached.isOk()) {
            return Future.succeededFuture(cached);
        }
        log.info("[ ZERO ] 验证加载：identifier = {} / provider = {}", identifier, this.getClass().getName());
        return this.findUser(identifier);
    }

    // --------------- 子类必须实现的方法
    protected abstract Future<UserAt> findUser(String id);

    protected Future<UserAt> userAtEphemeral(final MSUser user) {
        return Future.succeededFuture(UserSession.of().userAtEphemeral(user));
    }

    // --------------- 检查专用方法
    public Future<Boolean> isMatched(final LoginRequest request, final UserAt userAt) {
        final String credential = request.getCredential();
        final MSUser user = userAt.logged();
        if (Objects.isNull(user)) {
            return Future.succeededFuture(Boolean.FALSE);
        }
        final boolean verified = Objects.requireNonNull(this.strategy).verify(user.getPassword(), credential);
        return Future.succeededFuture(verified);
    }

    protected Future<Boolean> isMatched(final LoginRequest request, final UserAt userAt,
                                        final Duration duration) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(this.loginType(), duration);
        final String id = request.getId();
        final String codeStored = UserCache.of().authorize(id, captchaArgs);
        if (Objects.isNull(codeStored)) {
            return Future.succeededFuture(Boolean.FALSE);
        }
        final String code = request.getCredential();
        return Future.succeededFuture(codeStored.equals(code));
    }
}
