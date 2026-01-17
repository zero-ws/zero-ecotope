package io.zerows.plugins.security.ldap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.service.AsyncWallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@Wall(path = "/api/*", type = SecurityType.LDAP)
@Slf4j
public class LdapWallExecutor extends AsyncWallExecutor {
    @Override
    protected LdapLoginRequest createRequest(final JsonObject credentials) {
        final LdapLoginRequest request = new LdapLoginRequest();
        request.setUsername(Ut.valueString(credentials, KName.USERNAME));
        request.setPassword(Ut.valueString(credentials, KName.PASSWORD));
        return request;
    }
}
