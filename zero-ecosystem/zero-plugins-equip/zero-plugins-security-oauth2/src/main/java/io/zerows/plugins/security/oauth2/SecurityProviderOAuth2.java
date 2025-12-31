package io.zerows.plugins.security.oauth2;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityConstant;
import io.zerows.plugins.security.SecurityProvider;
import io.zerows.plugins.security.metadata.YmSecuritySpec;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-30
 */
@SPID("Security/OAUTH2")
@Slf4j
public class SecurityProviderOAuth2 implements SecurityProvider {
    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        final OAuth2Auth provider = (OAuth2Auth) this.configureProvider401(vertxRef, config);
        final String callback = Ut.valueString(options, YmSecuritySpec.oauth2.options.callback);
        return CC_HANDLER_401.pick(
            () -> OAuth2AuthHandler.create(vertxRef, provider, callback),
            String.valueOf(options.hashCode())
        );
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        log.info("{} 🔐 / 启用OAuth2：{}", SecurityConstant.K_PREFIX_SEC, options.encode());

        final OAuth2Options auth2Options = new OAuth2Options(options);
        return CC_PROVIDER_401.pick(
            () -> OAuth2Auth.create(vertxRef, auth2Options),
            String.valueOf(options.hashCode())
        );
    }
}
