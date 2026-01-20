package io.zerows.plugins.security.oauth2.server;

import java.util.LinkedHashSet;
import java.util.Set;

public interface AddrApi {
    String OAUTH2_AUTHORIZE = "/oauth2/authorize";
    String OAUTH2_TOKEN = "/oauth2/token";
    String OAUTH2_JWKS = "/oauth2/jwks";
    String OAUTH2_REVOKE = "/oauth2/revoke";
    String OAUTH2_INTROSPECT = "/oauth2/introspect";
    String OAUTH2_USERINFO = "/userinfo";
    Set<String> OAUTH2_APIS = new LinkedHashSet<>() {
        {
            this.add(OAUTH2_AUTHORIZE);
            this.add(OAUTH2_TOKEN);
            this.add(OAUTH2_JWKS);
            this.add(OAUTH2_REVOKE);
            this.add(OAUTH2_INTROSPECT);
            this.add(OAUTH2_USERINFO);
        }
    };
}
