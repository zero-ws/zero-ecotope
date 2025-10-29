package io.zerows.extension.commerce.rbac.osgi.spi.secure;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.epoch.metadata.security.TokenJwt;
import io.zerows.extension.commerce.rbac.plugins.authorization.ProfileProvider;
import io.zerows.extension.commerce.rbac.plugins.authorization.ProfileResource;
import io.zerows.plugins.security.authorization.AuthorizationBuiltInHandler;
import io.zerows.plugins.security.authorization.AuthorizationResource;
import io.zerows.sdk.security.OldLeeExtension;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class WallExtension implements OldLeeExtension {
    /*
     * 401 call internal workflow
     */
    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final SecurityMeta config) {
        //        final OldLeeBuiltIn internal = Ut.service(OldLeeBuiltIn.class);
        //        final SecurityMeta copy = config.copy().setType(SecurityType.JWT);
        //        return internal.authenticate(vertx, copy);
        return null;
    }

    @Override
    public AuthenticationProvider provider(final Vertx vertx, final SecurityMeta config) {
        //        final OldLeeBuiltIn internal = Ut.service(OldLeeBuiltIn.class);
        //        final SecurityMeta copy = config.copy().setType(SecurityType.JWT);
        //        return internal.provider(vertx, copy);
        return null;
    }

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final SecurityMeta config) {
        // Ignore handler class mode
        final AuthorizationResource resource = ProfileResource.create(config);
        final AuthorizationHandler handler = AuthorizationBuiltInHandler.create(resource);
        final AuthorizationProvider provider = ProfileProvider.provider(config);
        handler.addAuthorizationProvider(provider);
        // Ignore defined
        return handler;
    }

    @Override
    public String encode(final JsonObject data, final SecurityConfig config) {
        return TokenJwt.encode(data);
    }

    @Override
    public JsonObject decode(final String token, final SecurityConfig config) {
        return TokenJwt.decode(token);
    }
}
