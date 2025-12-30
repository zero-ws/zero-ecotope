package io.zerows.plugins.security.htpasswd;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-31
 */
@SPID("Security/HT_PASSWD")
@Slf4j
public class SecurityProviderHtpasswd implements SecurityProvider {
    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config) {
        throw new _501NotSupportException("[ ZERO ] Security/HT_PASSWD 不支持 401 Handler 构造");
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        log.info("🔐 / 启用HT_PASSWD：{}", options.encode());

        final HtpasswdAuthOptions htpasswdOptions = new HtpasswdAuthOptions(options);
        return CC_PROVIDER_401.pick(
            () -> HtpasswdAuth.create(vertxRef, htpasswdOptions),
            String.valueOf(options.hashCode())
        );
    }
}
