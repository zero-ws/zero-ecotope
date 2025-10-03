package io.zerows.plugins.common.security.authenticate;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.component.log.Annal;
import io.zerows.component.environment.DevEnv;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.epoch.corpus.security.Against;
import io.zerows.epoch.corpus.web.cache.Rapid;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AuthenticateGateway {

    private static final Annal LOGGER = Annal.get(AuthenticateGateway.class);

    public static void userCached(final JsonObject credentials, final Actuator actuator, final Actuator fnCache) {
        final String habitus = credentials.getString(KName.HABITUS);
        final Rapid<String, JsonObject> rapid = Rapid.object(habitus);
        rapid.read(KWeb.CACHE.User.AUTHENTICATE).onComplete(res -> {
            if (res.succeeded()) {
                final JsonObject cached = res.result();
                if (Objects.isNull(cached)) {
                    Fn.jvmAt(actuator);
                } else {
                    if (DevEnv.devAuthorized()) {
                        LOGGER.info("[ Auth ]\u001b[0;32m 401 Authenticated Cached successfully!\u001b[m");
                    }
                    Fn.jvmAt(fnCache);
                }
            }
        });
    }

    public static void userCached(final JsonObject credentials, final Actuator actuator) {
        final String habitus = credentials.getString(KName.HABITUS);
        final Rapid<String, JsonObject> rapid = Rapid.object(habitus);
        rapid.write(KWeb.CACHE.User.AUTHENTICATE, credentials).onComplete(next -> Fn.jvmAt(actuator));
    }

    /*
     *  Executing Aegis Method and this code split because of re-use in two points
     *  1) AuthenticateBuiltInProvider code     HTTP Workflow
     *  2) SicStompServerHandler       code     WebSocket Workflow
     */
    public static void userVerified(final JsonObject credentials, final Aegis aegis, final Handler<AsyncResult<Boolean>> handler) {
        final Against against = aegis.getAuthorizer();
        final Method method = against.getAuthenticate();
        if (Objects.isNull(method)) {
            // Exception for method is null ( This situation should not happen )
            handler.handle(FnVertx.failOut(_401UnauthorizedException.class, "[ ZERO ] 认证权限不够！"));
        } else {
            // Verify the data by @Wall's method that has been annotated by @Authenticate
            final Object proxy = aegis.getProxy();
            final Future<Boolean> checkedFuture = Ut.invokeAsync(proxy, method, credentials);
            checkedFuture.onComplete(res -> {
                if (res.succeeded()) {
                    Boolean checked = res.result();
                    checked = !Objects.isNull(checked) && checked;
                    handler.handle(Future.succeededFuture(checked));
                } else {
                    // Exception Throw
                    final Throwable ex = res.cause();
                    if (Objects.isNull(ex)) {
                        // 401 Without Exception
                        handler.handle(FnVertx.failOut(_401UnauthorizedException.class, "[ ZERO ] 认证权限不够！"));
                    } else {
                        // 401 With Throwable
                        handler.handle(Future.failedFuture(ex));
                    }
                }
            });
        }
    }
}
