package io.zerows.plugins.security;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ProfileResource {

    static ProfileResource buildIn(final SecurityMeta aegis) {
        return new ProfileResourceImpl(aegis);
    }

    void requestResource(RoutingContext context, Handler<AsyncResult<Authorization>> handler);
}
