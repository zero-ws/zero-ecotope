package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.platform.enums.EmSecure;
import io.zerows.sdk.security.Lee;
import io.zerows.sdk.security.LeeBuiltIn;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BridgeLee implements LeeBuiltIn {
    private static final Cc<String, Lee> CC_LEE = Cc.openThread();

    private static final ConcurrentMap<EmSecure.SecurityType, Supplier<Lee>> LEE_SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(EmSecure.SecurityType.BASIC, LeeBasic::new);
            this.put(EmSecure.SecurityType.HT_DIGEST, LeeDigest::new);
            this.put(EmSecure.SecurityType.JWT, LeeJwt::new);
            this.put(EmSecure.SecurityType.OAUTH2, LeeOAuth2::new);
        }
    };

    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final KSecurity config) {
        final Lee reference = this.component(config.getType());
        return reference.authenticate(vertx, config);
    }

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final KSecurity config) {
        final Lee reference = this.component(config.getType());
        return reference.authorization(vertx, config);
    }

    @Override
    public AuthenticationProvider provider(final Vertx vertx, final KSecurity config) {
        final Lee reference = this.component(config.getType());
        return reference.provider(vertx, config);
    }

    @Override
    public String encode(final JsonObject data, final KSecurity.Provider config) {
        final Lee reference = this.component(config.wall());
        return reference.encode(data, config);
    }

    @Override
    public JsonObject decode(final String token, final KSecurity.Provider config) {
        final Lee reference = this.component(config.wall());
        return reference.decode(token, config);
    }

    private Lee component(final EmSecure.SecurityType wall) {
        final Supplier<Lee> supplier = LEE_SUPPLIER.getOrDefault(wall, null);
        Objects.requireNonNull(supplier);
        return CC_LEE.pick(supplier, wall.key()); // Fn.po?lThread(LEE_POOL, supplier, wall.key());
    }
}
