package io.zerows.plugins.security.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Wall;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.WallExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认规范，所有 secure 相关的接口都放在 /api/* 路径之下
 * <pre>
 *     1. /api/*        - 所有安全相关的接口
 *     2. /auth/*       - 认证和授权相关接口
 *     3. name = master
 * </pre>
 */
@Wall(path = "/api/*", type = SecurityType.BASIC)
@Slf4j
public class BasicWallExecutor implements WallExecutor {
    @Override
    public Future<Boolean> authenticate(final JsonObject credentials) {
        return null;
    }

    @Override
    public Future<JsonObject> authorize(final User user) {
        return null;
    }

    @Override
    public Future<JsonObject> resource(final JsonObject params) {
        return null;
    }
}
