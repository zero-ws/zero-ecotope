package io.zerows.plugins.security.oauth2.server;

interface Addr {

    String PREFIX = "Security.OAUTH2.";

    String AUTHORIZE = PREFIX + "AUTHORIZE";
    String TOKEN = PREFIX + "TOKEN";
    String JWKS = PREFIX + "JWKS";
    String REVOKE = PREFIX + "REVOKE";
    String INTROSPECT = PREFIX + "INTROSPECT";
    String USERINFO = PREFIX + "USERINFO";
}
