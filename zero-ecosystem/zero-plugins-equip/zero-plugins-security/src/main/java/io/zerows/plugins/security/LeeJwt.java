package io.zerows.plugins.security;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.impl.jose.JWT;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.plugins.security.authenticate.AdapterProvider;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class LeeJwt extends AbstractLee {
    private static final Cc<String, JWTAuth> CC_PROVIDER = Cc.openThread();

    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final KSecurity config) {
        final JWTAuth provider = this.providerInternal(vertx, config);
        // Jwt Handler Generated
        final String realm = this.option(config, YmlCore.secure.options.REALM);
        final JWTAuthHandler standard;
        if (Ut.isNil(realm)) {
            standard = JWTAuthHandler.create(provider);
        } else {
            standard = JWTAuthHandler.create(provider, realm);
        }
        return this.wrapHandler(standard, config);
    }

    @Override
    public AuthenticationProvider provider(final Vertx vertx, final KSecurity config) {
        final JWTAuth standard = this.providerInternal(vertx, config);
        final AdapterProvider extension = AdapterProvider.extension(standard);
        return extension.provider(config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JWTAuth providerInternal(final Vertx vertx, final KSecurity config) {
        // Options
        final KSecurity.Provider provider = config.item();
        return this.provider(vertx, provider);
    }

    private JWTAuth provider(final Vertx vertx, final KSecurity.Provider provider) {
        final JWTAuthOptions options = new JWTAuthOptions(provider.options());
        final String key = provider.wall().name() + options.hashCode();
        return CC_PROVIDER.pick(() -> JWTAuth.create(vertx, options), key);
    }

    @Override
    public String encode(final JsonObject data, final KSecurity.Provider config) {
        final JWTAuth provider = this.provider(Ux.nativeVertx(), config);
        return provider.generateToken(data);
    }

    @Override
    public JsonObject decode(final String token, final KSecurity.Provider config) {
        final JWTAuth provider = this.provider(Ux.nativeVertx(), config);
        final JWT jwt = Ut.field(provider, "jwt");
        return Objects.isNull(jwt) ? new JsonObject() : Fn.jvmOr(() -> jwt.decode(token));
    }
}
