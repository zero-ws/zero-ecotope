package io.zerows.plugins.security.jwt;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityProvider;
import io.zerows.plugins.security.metadata.YmSecuritySpec;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2025-12-31
 */
@SPID("Security/JWT")
@Slf4j
public class JwtSecurityProvider implements SecurityProvider {
    private static final AtomicBoolean IS_LOG = new AtomicBoolean(Boolean.TRUE);

    @Override
    public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config,
                                                     final AuthenticationProvider authProvider) {
        final boolean isEnabled = config.option("enabled", Boolean.FALSE);
        if (!isEnabled) {
            return null;
        }
        final JsonObject options = config.options();
        final JWTAuth provider = this.findProvider(vertxRef, options, authProvider);
        final String realm = Ut.valueString(options, YmSecuritySpec.jwt.options.realm);
        return CC_HANDLER_401.pick(
            () -> JWTAuthHandler.create(provider, realm),
            String.valueOf(options.hashCode())
        );
    }

    private JWTAuth findProvider(final Vertx vertxRef, final JsonObject options,
                                 final AuthenticationProvider authProvider) {
        final JWTAuth found;
        if (authProvider instanceof final ChainAuth chainAuth) {
            final List<AuthenticationProvider> providers = Ut.field(chainAuth, "providers");
            if (Objects.isNull(providers) || providers.isEmpty()) {
                found = this.findProvider(vertxRef, options);
            } else {
                found = providers.stream()
                    .filter(item -> item instanceof JWTAuth)
                    .map(item -> (JWTAuth) item)
                    .findFirst()
                    .orElseGet(() -> this.findProvider(vertxRef, options));
            }
        } else {
            found = this.findProvider(vertxRef, options);
        }
        return found;
    }

    @Override
    public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
        final boolean isEnabled = config.option("enabled", Boolean.FALSE);
        if (!isEnabled) {
            return null;
        }
        final JsonObject options = config.options();
        if (IS_LOG.getAndSet(Boolean.FALSE)) {
            log.info("[ PLUG ] 🔐 / 启用JWT：{}", options.encode());
        }
        return this.findProvider(vertxRef, options);
    }

    private JWTAuth findProvider(final Vertx vertxRef, final JsonObject options) {
        final JWTAuthOptions jwtOptions = new JWTAuthOptions(options);
        return (JWTAuth) CC_PROVIDER_401.pick(
            () -> JWTAuth.create(vertxRef, jwtOptions),
            String.valueOf(options.hashCode())
        );
    }
}
