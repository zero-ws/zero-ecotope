package io.zerows.plugins.security.jwt;

import io.r2mo.jaas.auth.LoginRequest;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.security.SecurityConstant;
import io.zerows.plugins.security.service.AsyncWallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@Wall(path = "/api/*", type = SecurityConstant.WALL_JWT)
@Slf4j
public class JwtWallExecutor extends AsyncWallExecutor {
    @Override
    protected LoginRequest createRequest(final JsonObject credentials) {
        final JwtLoginRequest request = new JwtLoginRequest();
        request.setUsername(Ut.valueString(credentials, KName.USERNAME));
        request.setPassword(Ut.valueString(credentials, KName.PASSWORD));
        return request;
    }
}
