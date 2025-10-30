package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author lang : 2025-10-29
 */
class AuthorizationCommonProvider implements AuthorizationProvider {

    private final static SecurityManager MANAGER = SecurityManager.of();
    private final SecurityMeta meta;
    private final SecurityConfig config;

    public AuthorizationCommonProvider(final SecurityMeta meta) {
        this.meta = meta;
        this.config = MANAGER.configOf(meta.getType());
    }

    @Override
    public String getId() {
        // 和 SecurityType 执行绑定，得到对应信息
        return this.meta.getType().key();
    }

    /**
     * 授权专用方法，针对已经登录的用户进行授权
     *
     * @param user 查询用户
     *
     * @return 授权结果
     */
    @Override
    public Future<Void> getAuthorizations(final User user) {

        return Future.succeededFuture();
    }
}
