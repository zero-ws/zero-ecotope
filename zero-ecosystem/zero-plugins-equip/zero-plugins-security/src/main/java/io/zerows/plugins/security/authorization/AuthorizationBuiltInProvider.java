package io.zerows.plugins.security.authorization;

import io.r2mo.function.Fn;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.profile.PermissionAuthorization;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AuthorizationBuiltInProvider implements AuthorizationProvider {

    private transient final SecurityMeta aegis;

    private AuthorizationBuiltInProvider(final SecurityMeta aegis) {
        this.aegis = aegis;
    }

    public static AuthorizationProvider provider(final SecurityMeta aegis) {
        return new AuthorizationBuiltInProvider(aegis);
    }

    @Override
    public String getId() {
        return this.aegis.getType().key();
    }

    @Override
    public Future<Void> getAuthorizations(final User user) {
        return null;
    }

    @SuppressWarnings("all")
    public void getAuthorizations(final User user, final Handler<AsyncResult<Void>> handler) {
        final Method method = null; // this.aegis.getAuthorizer().getAuthorization();
        Fn.jvmAt(() -> {
            /*
             * Future<Set<String>> fetching
             */
            final Future<Set<String>> future = (Future<Set<String>>) method.invoke(this.aegis.getProxy(), user);
            future.onComplete(res -> {
                if (res.succeeded()) {
                    final Set<String> permissionSet = res.result();
                    final Authorization authorization = PermissionAuthorization.create(permissionSet);
                    user.authorizations().put(this.getId(), authorization);
                    handler.handle(Future.succeededFuture());
                } else {
                    final Throwable ex = res.cause();
                    handler.handle(Future.failedFuture(ex));
                }
            });
        });
    }
}
