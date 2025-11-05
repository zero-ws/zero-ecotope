package io.zerows.extension.module.rbac.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.servicespec.AccreditStub;
import io.zerows.extension.module.rbac.servicespec.JwtStub;
import io.zerows.sdk.security.WallExecutor;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Interface defined for component
 */
@Wall(path = "/api/*")
@Slf4j
public class RbacWall implements WallExecutor {
    @Inject
    private transient JwtStub jwtStub;
    @Inject
    private transient AccreditStub accredit;

    @Override
    public Future<Boolean> authenticate(final JsonObject data) {
        final String token = data.getString(KName.ACCESS_TOKEN);
        final String user = data.getString(KName.USER);
        // No Cache
        log.info("[ MOD ] 验证用户数据和令牌 data = {} / token = {}", user, token);
        return this.jwtStub.verify(user, token);
    }

    @Override
    public Future<JsonObject> authorize(final User user) {
        return this.accredit.profile(user);
    }

    @Override
    public Future<JsonObject> resource(final JsonObject params) {
        return this.accredit.resource(params);
    }
}
