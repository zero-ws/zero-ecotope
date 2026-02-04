package io.zerows.plugins.weco;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Future;

import java.time.Duration;

@SPID
public class WeCoSessionVertx implements WeCoAsyncSession {

    @Override
    public void save(final String cacheKey, final String statusOr, final Duration expiredAt) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(TypeLogin.ID_WECHAT, expiredAt);
        final Kv<String, String> generated = Kv.create(cacheKey, statusOr);
        UserCache.of().authorize(generated, captchaArgs);
    }

    @Override
    public String get(final String uuid, final Duration expireSeconds) {
        throw new _501NotSupportException("[ PLUG ] ( WeCo ) 同步获取会话不被支持，请使用异步方法！");
    }

    @Override
    public Future<String> getAsync(final String cacheKey, final Duration expiredAt) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(TypeLogin.ID_WECHAT, expiredAt);
        return UserCache.of().authorize(cacheKey, captchaArgs).a();
    }
}
