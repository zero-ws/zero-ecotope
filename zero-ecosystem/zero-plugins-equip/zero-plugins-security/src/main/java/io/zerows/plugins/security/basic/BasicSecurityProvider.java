package io.zerows.plugins.security.basic;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import io.zerows.plugins.security.metadata.YmSecuritySpec;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@SPID("Security/BASIC")
@Slf4j
public class BasicSecurityProvider implements SecurityProvider {

    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config,
                                                     final AuthenticationProvider authProvider) {
        final JsonObject options = config.options();
        final String realm = Ut.valueString(options, YmSecuritySpec.basic.options.realm);
        return CC_HANDLER_401.pick(
            () -> BasicAuthHandler.create(authProvider, realm),
            String.valueOf(options.hashCode())
        );
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        // 2. 创建并缓存
        return null;
    }
}
