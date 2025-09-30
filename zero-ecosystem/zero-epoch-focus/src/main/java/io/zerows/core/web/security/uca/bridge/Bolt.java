package io.zerows.core.web.security.uca.bridge;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.util.Ut;
import io.zerows.module.security.atom.Aegis;
import io.zerows.module.security.zdk.Lee;
import io.zerows.module.security.zdk.LeeBuiltIn;
import io.zerows.module.security.zdk.LeeExtension;

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
