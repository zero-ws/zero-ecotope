package io.zerows.cosmic.plugins.security;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.component.log.LogO;
import io.zerows.cosmic.plugins.security.exception._40076Exception400WallSize;
import io.zerows.cosmic.plugins.security.exception._40077Exception400WallProviderConflict;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.epoch.metadata.security.AegisItem;
import io.zerows.platform.enums.EmSecure;
import io.zerows.sdk.security.Lee;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class BoltWhich implements Bolt {
    public static final String AUTH_401_METHOD = "[ Auth ] Your `@Wall` class missed @Authenticate method ! {0}";
    public static final String AUTH_401_SERVICE = "[ Auth ] Your `Lee` in service-loader /META-INF/services/ is missing....";
    public static final String AUTH_401_HANDLER = "[ Auth ] You have configured secure, but the authenticate handler is null! type = {0}";
    private static final LogO LOGGER = Ut.Log.security(BoltWhich.class);
    // LOGGER Control
    private static final AtomicBoolean[] LOG_LEE = new AtomicBoolean[]{
        new AtomicBoolean(Boolean.TRUE),
        new AtomicBoolean(Boolean.TRUE),
        new AtomicBoolean(Boolean.TRUE)
    };

    static Cc<String, Bolt> CC_BOLT = Cc.openThread();
    static Cc<String, Lee> CC_LEE = Cc.openThread();

    @Override
    public AuthenticationHandler authenticate(final Vertx vertx, final Aegis config) {
        Objects.requireNonNull(config);
        if (config.noAuthentication()) {
            // Log
            if (LOG_LEE[0].getAndSet(Boolean.FALSE)) {
                LOGGER.warn(AUTH_401_METHOD, config);
            }
            return null;
        }
        final Aegis verified = this.verifyAuthenticate(config);
        final Lee lee = Bolt.reference(config.getType());
        if (Objects.isNull(lee)) {
            // Log
            if (LOG_LEE[1].getAndSet(Boolean.FALSE)) {
                LOGGER.warn(AUTH_401_SERVICE, config.getType());
            }
            return null;
        }
        final AuthenticationHandler handler = lee.authenticate(vertx, verified);
        if (Objects.isNull(handler)) {
            // Log
            if (LOG_LEE[2].getAndSet(Boolean.FALSE)) {
                LOGGER.warn(AUTH_401_HANDLER, config.getType());
            }
        }
        return handler;
    }

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final Aegis config) {
        Objects.requireNonNull(config);
        if (config.noAuthorization()) {
            return null;
        }
        final Lee lee = Bolt.reference(config.getType());
        if (Objects.isNull(lee)) {
            return null;
        }
        return lee.authorization(vertx, config);
    }

    @Override
    public AuthenticationProvider authenticateProvider(final Vertx vertx, final Aegis config) {
        Objects.requireNonNull(config);
        if (config.noAuthentication()) {
            return null;
        }
        final Lee lee = Bolt.reference(config.getType());
        if (Objects.isNull(lee)) {
            return null;
        }
        return lee.provider(vertx, config);
    }

    /*
     * Here the validation rules
     * 1. The size of provider should be matched
     * - Extension could be > 1
     * - Others must be = 1
     * 2. All the following must be match
     * - JWT, WEB, OAUTH2, DIGEST
     * They are fixed provider of authenticate
     */
    private Aegis verifyAuthenticate(final Aegis config) {
        if (EmSecure.AuthWall.EXTENSION != config.getType()) {
            /*
             * The size should be 1 ( For non-extension )
             */
            final AegisItem item = config.item();
            Fn.jvmKo(Objects.isNull(item), _40076Exception400WallSize.class, config.getType(), 1);
        }
        final Set<Class<?>> provider = config.providers();
        /*
         * Must be valid type of provider
         */
        provider.forEach(item -> Fn.jvmKo(!AuthenticationProvider.class.isAssignableFrom(item),
            _40077Exception400WallProviderConflict.class, item));
        return config;
    }
}
