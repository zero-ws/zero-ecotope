package io.zerows.epoch.corpus.web.security.uca.bridge;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.platform.enums.EmSecure;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.security.Lee;
import io.zerows.epoch.sdk.security.LeeBuiltIn;
import io.zerows.epoch.sdk.security.LeeExtension;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Bolt {
    static Bolt get() {
        return BoltWhich.CC_BOLT.pick(BoltWhich::new);
    }

    static Lee reference(final EmSecure.AuthWall wall) {
        //final AuthWall wall = config.getType();
        if (EmSecure.AuthWall.EXTENSION == wall) {
            return BoltWhich.CC_LEE.pick(() -> Ut.service(LeeExtension.class), LeeExtension.class.getName());
        } else {
            return BoltWhich.CC_LEE.pick(() -> Ut.service(LeeBuiltIn.class), LeeBuiltIn.class.getName());
        }
    }

    /*
     * 1. Authenticate Handler
     */
    AuthenticationHandler authenticate(Vertx vertx, Aegis config);

    /*
     * 2. Authorization Handler
     */
    AuthorizationHandler authorization(Vertx vertx, Aegis config);

    AuthenticationProvider authenticateProvider(Vertx vertx, Aegis config);
}
