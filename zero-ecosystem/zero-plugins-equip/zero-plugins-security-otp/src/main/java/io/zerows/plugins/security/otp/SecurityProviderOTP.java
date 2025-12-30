package io.zerows.plugins.security.otp;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.otp.hotp.HotpAuth;
import io.vertx.ext.auth.otp.hotp.HotpAuthOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.OtpAuthHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityConstant;
import io.zerows.plugins.security.SecurityProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-30
 */
@SPID("Security/OTP")
@Slf4j
public class SecurityProviderOTP implements SecurityProvider {
    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config) {
        final HotpAuth provider = (HotpAuth) this.configureProvider401(vertxRef, config);
        return CC_HANDLER_401.pick(
            () -> OtpAuthHandler.create(provider),
            String.valueOf(config.hashCode())
        );
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final JsonObject options = config.options();
        log.info("{} 🔐 / 启用OAuth2：{}", SecurityConstant.K_PREFIX_SEC, options.encode());

        final HotpAuthOptions hotpAuthOptions = new HotpAuthOptions(options);
        return CC_PROVIDER_401.pick(
            () -> HotpAuth.create(hotpAuthOptions),
            String.valueOf(options.hashCode())
        );
    }
}
