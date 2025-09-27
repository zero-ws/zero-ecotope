package io.zerows.plugins.common.security.authorization;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
io.r2mo.function.Actuator;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.web.cache.Rapid;
import io.zerows.module.metadata.uca.environment.DevEnv;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AuthorizationCache {

    private static final Annal LOGGER = Annal.get(AuthorizationCache.class);

    private static String requestKey(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        // Recovery will let your api/:actor/search abstract uri security issue
        // final String uri = ZeroAnno.recoveryUri(request.path(), request.method());
        return request.method() + " " + request.path();
    }

    static void userAuthorized(final RoutingContext context, final Actuator actuator) {
        final User user = context.user();
        final Rapid<String, JsonObject> rapid = Rapid.user(user);
        rapid.read(KWeb.CACHE.User.AUTHORIZATION).onComplete(res -> {
            if (res.succeeded()) {
                JsonObject authorized = res.result();
                if (Objects.isNull(authorized)) {
                    authorized = new JsonObject();
                }
                final String requestKey = requestKey(context);
                if (authorized.getBoolean(requestKey, Boolean.FALSE)) {
                    final String habitus = user.principal().getString(KName.HABITUS);
                    if (DevEnv.devAuthorized()) {
                        LOGGER.info("[ Auth ]\u001b[0;32m 403 Authorized Cached successfully \u001b[m for ( {1}, {0} )", habitus, requestKey);
                    }
                    context.next();
                } else {
                    actuator.execute();
                }
            }
        });
    }

    static void userAuthorize(final RoutingContext context, final Actuator actuator) {
        final User user = context.user();
        final Rapid<String, JsonObject> rapid = Rapid.user(user);
        rapid.read(KWeb.CACHE.User.AUTHORIZATION).onComplete(res -> {
            if (res.succeeded()) {
                final String requestKey = requestKey(context);
                JsonObject authorized = res.result();
                if (Objects.isNull(authorized)) {
                    authorized = new JsonObject();
                }
                authorized.put(requestKey, Boolean.TRUE);
                rapid.write(KWeb.CACHE.User.AUTHORIZATION, authorized).onComplete(next -> actuator.execute());
            }
        });
    }
}
