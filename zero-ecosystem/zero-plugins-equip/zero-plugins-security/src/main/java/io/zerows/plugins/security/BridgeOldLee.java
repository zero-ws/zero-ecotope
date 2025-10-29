package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.OldLee;
import io.zerows.sdk.security.OldLeeBuiltIn;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BridgeOldLee implements OldLeeBuiltIn {
    private static final Cc<String, OldLee> CC_LEE = Cc.openThread();

    private static final ConcurrentMap<SecurityType, Supplier<OldLee>> LEE_SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(SecurityType.BASIC, OldLeeBasic::new);
            this.put(SecurityType.HT_DIGEST, OldLeeDigest::new);
            this.put(SecurityType.JWT, OldLeeJwt::new);
            this.put(SecurityType.OAUTH2, OldLeeOAuth2::new);
        }
    };

    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final SecurityMeta config) {
        final OldLee reference = this.component(config.getType());
        return reference.authenticate(vertx, config);
    }

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final SecurityMeta config) {
        final OldLee reference = this.component(config.getType());
        return reference.authorization(vertx, config);
    }

    @Override
    public AuthenticationProvider provider(final Vertx vertx, final SecurityMeta config) {
        final OldLee reference = this.component(config.getType());
        return reference.provider(vertx, config);
    }

    @Override
    public String encode(final JsonObject data, final SecurityConfig config) {
        final OldLee reference = this.component(config.type());
        return reference.encode(data, config);
    }

    @Override
    public JsonObject decode(final String token, final SecurityConfig config) {
        final OldLee reference = this.component(config.type());
        return reference.decode(token, config);
    }

    private OldLee component(final SecurityType wall) {
        final Supplier<OldLee> supplier = LEE_SUPPLIER.getOrDefault(wall, null);
        Objects.requireNonNull(supplier);
        return CC_LEE.pick(supplier, wall.key()); // Fn.po?lThread(LEE_POOL, supplier, wall.key());
    }
}
