package io.zerows.extension.commerce.rbac.plugins.authorization;

import io.r2mo.function.Fn;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.corpus.security.Aegis;
import io.zerows.epoch.program.Ut;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ProfileProvider implements AuthorizationProvider {

    private transient final Aegis aegis;

    private ProfileProvider(final Aegis aegis) {
        this.aegis = aegis;
    }

    public static AuthorizationProvider provider(final Aegis aegis) {
        return new ProfileProvider(aegis);
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
        final Method method = this.aegis.getAuthorizer().getAuthorization();
        Fn.jvmAt(() -> {
            /*
             * Future<Set<String>> fetching
             */
            final Future<JsonObject> future = (Future<JsonObject>) method.invoke(this.aegis.getProxy(), user);
            future.onComplete(res -> {
                if (res.succeeded()) {
                    final ConcurrentMap<String, Set<String>> profiles = new ConcurrentHashMap<>();
                    Ut.<JsonArray>itJObject(res.result(), (values, field) -> profiles.put(field, Ut.toSet(values)));
                    final Authorization required = ProfileAuthorization.create(profiles);
                    user.authorizations().put(this.getId(), required);
                    handler.handle(Future.succeededFuture());
                } else {
                    final Throwable ex = res.cause();
                    handler.handle(Future.failedFuture(ex));
                }
            });
        });
    }
}
