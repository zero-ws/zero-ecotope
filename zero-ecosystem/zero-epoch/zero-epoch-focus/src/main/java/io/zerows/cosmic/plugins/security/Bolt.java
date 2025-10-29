package io.zerows.cosmic.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.OldLee;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Deprecated
public interface Bolt {
    static Bolt get() {
        return BoltWhich.CC_BOLT.pick(BoltWhich::new);
    }

    static OldLee reference(final SecurityType wall) {
        //final AuthWall wall = config.getType();
        //        if (SecurityType.EXTENSION == wall) {
        //            return BoltWhich.CC_LEE.pick(() -> Ut.service(OldLeeExtension.class), OldLeeExtension.class.getName());
        //        } else {
        //            return BoltWhich.CC_LEE.pick(() -> Ut.service(OldLeeBuiltIn.class), OldLeeBuiltIn.class.getName());
        //        }
        return null;
    }

    /*
     * 1. Authenticate Handler
     */
    AuthenticationHandler authenticate(Vertx vertx, SecurityMeta config);

    /*
     * 2. Authorization Handler
     */
    AuthorizationHandler authorization(Vertx vertx, SecurityMeta config);

    AuthenticationProvider authenticateProvider(Vertx vertx, SecurityMeta config);
}
