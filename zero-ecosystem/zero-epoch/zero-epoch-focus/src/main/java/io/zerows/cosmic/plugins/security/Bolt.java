package io.zerows.cosmic.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.platform.enums.EmSecure;
import io.zerows.sdk.security.Lee;
import io.zerows.sdk.security.LeeBuiltIn;
import io.zerows.sdk.security.LeeExtension;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Bolt {
    static Bolt get() {
        return BoltWhich.CC_BOLT.pick(BoltWhich::new);
    }

    static Lee reference(final EmSecure.SecurityType wall) {
        //final AuthWall wall = config.getType();
        if (EmSecure.SecurityType.EXTENSION == wall) {
            return BoltWhich.CC_LEE.pick(() -> Ut.service(LeeExtension.class), LeeExtension.class.getName());
        } else {
            return BoltWhich.CC_LEE.pick(() -> Ut.service(LeeBuiltIn.class), LeeBuiltIn.class.getName());
        }
    }

    /*
     * 1. Authenticate Handler
     */
    AuthenticationHandler authenticate(Vertx vertx, KSecurity config);

    /*
     * 2. Authorization Handler
     */
    AuthorizationHandler authorization(Vertx vertx, KSecurity config);

    AuthenticationProvider authenticateProvider(Vertx vertx, KSecurity config);
}
