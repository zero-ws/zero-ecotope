package io.zerows.plugins.security.authorization;

import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface AuthorizationExtensionHandler extends AuthorizationHandler {

    default AuthorizationExtensionHandler configure(final SecurityMeta aegis) {
        return this;
    }
}
