package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ProfileResource {

    static ProfileResource buildIn(final SecurityMeta meta) {
        return new ProfileResourceImpl(meta);
    }

    Future<Authorization> requestResource(RoutingContext context);
}
