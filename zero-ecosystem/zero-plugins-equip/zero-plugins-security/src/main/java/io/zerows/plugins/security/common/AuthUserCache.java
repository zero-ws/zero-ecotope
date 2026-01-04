package io.zerows.plugins.security.common;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtSecurity;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;

/**
 * 用户缓存适配器
 * <p>
 * 适配策略调整：
 * 1. 缓存工厂 (MemoAtSecurity) 现在直接返回缓存实例 (MemoAt)，无需 await。
 * 2. 缓存实例的操作 (put/find/remove) 返回 Future，需要使用 {@link Future#await} 等待结果。
 * </p>
 */
@Slf4j
@SPID(priority = 207)
public class AuthUserCache implements UserCache {

    private static final Cc<String, MemoAtSecurity> CC_FACTORY = Cc.openThread();

    private MemoAtSecurity factory() {
        return CC_FACTORY.pick(AuthMemoAtSecurity::new);
    }

    // -------------------------------------------------------------------------
    // 登录态存储 (UserContext & UserAt)
    // -------------------------------------------------------------------------

    @Override
    public void login(final UserContext context) {
        if (context == null) {
            return;
        }

        // 1. 直接获取缓存实例
        final var cache = this.factory().userContext();
        // 2. 阻塞等待写入完成
        Future.await(cache.put(context.id().toString(), context));

        this.cacheVector(context.logged());
    }

    @Override
    public void login(final UserAt userAt) {
        if (userAt == null) {
            return;
        }

        final var cache = this.factory().userAt();
        Future.await(cache.put(userAt.id().toString(), userAt));

        this.cacheVector(userAt.logged());
    }

    private void cacheVector(final MSUser user) {
        if (user == null) {
            return;
        }
        final Set<String> idKeys = user.ids();
        final String uidStr = user.getId().toString();

        // 提取缓存实例，循环内直接调用
        final var vectorCache = this.factory().userVector();

        // 循环同步写入
        idKeys.forEach(idKey ->
            Future.await(vectorCache.put(idKey, uidStr))
        );
    }

    @Override
    public void logout(final UUID userId) {
        if (userId == null) {
            return;
        }
        final String uidStr = userId.toString();

        Future.await(this.factory().userAt().remove(uidStr));
        Future.await(this.factory().userContext().remove(uidStr));
    }

    // -------------------------------------------------------------------------
    // 登录态查找
    // -------------------------------------------------------------------------

    @Override
    public UserContext context(final UUID id) {
        if (id == null) {
            return null;
        }

        return Future.await(this.factory().userContext().find(id.toString()));
    }

    @Override
    public UserAt find(final String idOr) {
        if (idOr == null) {
            return null;
        }

        // 1. 查找索引
        final String uidStr = Future.await(this.factory().userVector().find(idOr));

        if (uidStr == null) {
            return null;
        }

        // 2. 递归查找详情
        return this.find(UUID.fromString(uidStr));
    }

    @Override
    public UserAt find(final UUID id) {
        if (id == null) {
            return null;
        }

        return Future.await(this.factory().userAt().find(id.toString()));
    }

    // -------------------------------------------------------------------------
    // 验证码 / 会话
    // -------------------------------------------------------------------------

    @Override
    public void authorize(final Kv<String, String> generated, final CaptchaArgs config) {
        if (generated == null) {
            return;
        }

        Future.await(
            this.factory().ofAuthorize(config)
                .put(generated.key(), generated.value())
        );

        log.info("[ ZERO ] 验证码缓存写入：Key = {}, expiredAt = {}", generated.key(), config.duration());
    }

    @Override
    public String authorize(final String consumerId, final CaptchaArgs config) {
        if (consumerId == null) {
            return null;
        }

        return Future.await(
            this.factory().ofAuthorize(config).find(consumerId)
        );
    }

    @Override
    public void authorizeKo(final String consumerId, final CaptchaArgs config) {
        if (consumerId == null) {
            return;
        }

        Future.await(
            this.factory().ofAuthorize(config).remove(consumerId)
        );

        log.info("[ ZERO ] 验证码缓存清除：Key = {}", consumerId);
    }

    // -------------------------------------------------------------------------
    // 令牌管理
    // -------------------------------------------------------------------------

    @Override
    public void token(final String token, final UUID userId) {
        if (token == null || userId == null) {
            return;
        }

        Future.await(
            this.factory().ofToken().put(token, userId.toString())
        );
    }

    @Override
    public UUID token(final String token) {
        if (token == null) {
            return null;
        }

        final String uidStr = Future.await(
            this.factory().ofToken().find(token)
        );

        return uidStr == null ? null : UUID.fromString(uidStr);
    }

    @Override
    public boolean tokenKo(final String token) {
        if (token == null) {
            return false;
        }

        Future.await(this.factory().ofToken().remove(token));
        return true;
    }

    @Override
    public void tokenRefresh(final String refreshToken, final UUID userId) {
        if (refreshToken == null || userId == null) {
            return;
        }

        Future.await(
            this.factory().ofRefresh().put(refreshToken, userId.toString())
        );
    }

    @Override
    public UUID tokenRefresh(final String refreshToken) {
        if (refreshToken == null) {
            return null;
        }

        final String uidStr = Future.await(
            this.factory().ofRefresh().find(refreshToken)
        );

        return uidStr == null ? null : UUID.fromString(uidStr);
    }

    @Override
    public boolean tokenRefreshKo(final String refreshToken) {
        if (refreshToken == null) {
            return false;
        }

        Future.await(this.factory().ofRefresh().remove(refreshToken));
        return true;
    }
}