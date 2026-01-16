package io.zerows.plugins.security.basic;

import io.r2mo.jaas.auth.LoginRequest;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.service.AsyncWallExecutor;
import io.zerows.support.Ut;
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
public class BasicWallExecutor extends AsyncWallExecutor {

    @Override
    protected LoginRequest createRequest(final JsonObject credentials) {
        final BasicLoginRequest request = new BasicLoginRequest();
        request.setUsername(Ut.valueString(credentials, KName.USERNAME));
        request.setPassword(Ut.valueString(credentials, KName.PASSWORD));
        return request;
    }
}
