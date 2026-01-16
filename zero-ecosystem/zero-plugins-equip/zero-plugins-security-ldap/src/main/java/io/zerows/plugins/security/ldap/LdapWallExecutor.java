package io.zerows.plugins.security.ldap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Wall;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.service.AsyncWallExecutor;
import lombok.extern.slf4j.Slf4j;

@Wall(path = "/api/*", type = SecurityType.LDAP)
@Slf4j
public class LdapWallExecutor extends AsyncWallExecutor {
    @Override
    protected LdapLoginRequest createRequest(final JsonObject credentials) {
        final LdapLoginRequest request = new LdapLoginRequest();
        return request;
    }
}
