package io.zerows.plugins.security.ldap;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2025-12-31
 */
@SPID("Security/LDAP")
@Slf4j
public class LdapSecurityProvider implements SecurityProvider {
    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config,
                                                     final AuthenticationProvider authProvider) {
        return null;
        // throw new _501NotSupportException("[ PLUG ] Security/LDAP 不支持 401 Handler 构造");
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final boolean isEnabled = config.option("enabled", Boolean.FALSE);
        if (!isEnabled) {
            return null;
        }

        final JsonObject options = config.option(KName.OPTIONS, new JsonObject());
        if (IS_LOG.getAndSet(Boolean.FALSE)) {
            log.info("[ PLUG ] 🔐 / 启用LDAP：{}", options.encode());
        }

        return CC_PROVIDER_401.pick(
            () -> LdapManager.of(vertxRef).createProvider(options),
            String.valueOf(options.hashCode())
        );
    }
}
