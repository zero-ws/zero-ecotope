package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoAtSecurity;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.plugins.cache.CachedFactory;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.spi.HPI;

import java.time.Duration;
import java.util.Objects;

/**
 * Base 中已经包含了 MemoAt 的基础缓存，所以此处可直接创建，唯一问题在于要构造不同的
 */
class AuthMemoAtSecurity implements MemoAtSecurity {

    private static final Cc<String, MemoAt<?, ?>> CC_CACHE = Cc.open();
    private static final Cc<String, CachedFactory> CC_SECURITY = Cc.openThread();

    AuthMemoAtSecurity() {
    }

    private <K, V> MemoAt<K, V> memoAt(final String name, final Class<K> keyType, final Class<V> valueType,
                                       final Duration expiredAt) {
        final CachedFactory factory = CC_SECURITY.pick(() -> HPI.findOneOf(CachedFactory.class));
        final MemoOptions<K, V> options = new MemoOptions<>(this.getClass());
        options.classK(keyType).classV(valueType).name(name);
        if (Objects.nonNull(expiredAt)) {
            options.duration(expiredAt);
        }
        final StoreVertx storeVertx = StoreVertx.of();
        return factory.findBy(storeVertx.vertx(), options);
    }

    private void ensureAuthorize(final CaptchaArgs configuration) {
        final TypeLogin type = configuration.type();
        final YmSecurity configSecurity = SecurityActor.configuration();
        if (TypeLogin.CAPTCHA == type && !configSecurity.isCaptcha()) {
            throw new _501NotSupportException("[ R2MO ] 未启用图片验证码功能！");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, UserAt> userAt() {
        final String name = UserCache.NAME_USER_AT + "/ASYNC:" + this.getClass().getName();
        return (MemoAt<String, UserAt>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, UserAt.class, null),
            name
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, UserContext> userContext() {
        final String name = UserCache.NAME_USER_CONTEXT + "/ASYNC:" + this.getClass().getName();
        return (MemoAt<String, UserContext>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, UserContext.class, null),
            name
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, String> userVector() {
        final String name = UserCache.NAME_USER_VECTOR + "/ASYNC:" + this.getClass().getName();
        return (MemoAt<String, String>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, String.class, null),
            name
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, String> ofToken() {
        final String name = UserCache.NAME_TOKEN + "/ASYNC:" + this.getClass().getName();
        final YmSecurity configSecurity = SecurityActor.configuration();
        return (MemoAt<String, String>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, String.class, configSecurity.getLimit().expiredAt()),
            name
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, String> ofRefresh() {
        final String name = UserCache.NAME_REFRESH + "/ASYNC:" + this.getClass().getName();
        final YmSecurity configSecurity = SecurityActor.configuration();
        return (MemoAt<String, String>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, String.class, configSecurity.getLimit().refreshAt()),
            name
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemoAt<String, String> ofAuthorize(final CaptchaArgs configuration) {
        // 验证配置信息
        this.ensureAuthorize(configuration);

        final String name = UserCache.NAME_AUTHORIZE + "/ASYNC:@" + configuration.hashCode();
        return (MemoAt<String, String>) CC_CACHE.pick(
            () -> this.memoAt(name, String.class, String.class, configuration.duration()),
            name
        );
    }
}
