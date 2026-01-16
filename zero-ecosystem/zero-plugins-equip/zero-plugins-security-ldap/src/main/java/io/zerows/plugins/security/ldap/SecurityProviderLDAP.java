package io.zerows.plugins.security.ldap;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.ldap.LdapAuthentication;
import io.vertx.ext.auth.ldap.LdapAuthenticationOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-31
 */
@SPID("Security/LDAP")
@Slf4j
public class SecurityProviderLDAP implements SecurityProvider {
    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config,
                                                     final AuthenticationProvider authProvider) {
        throw new _501NotSupportException("[ PLUG ] Security/LDAP 不支持 401 Handler 构造");
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        log.info("🔐 / 启用LDAP：{}", options.encode());

        final LdapAuthenticationOptions ldapOptions = new LdapAuthenticationOptions(options);
        return CC_PROVIDER_401.pick(
            () -> LdapAuthentication.create(vertxRef, ldapOptions),
            String.valueOf(options.hashCode())
        );
    }
}
