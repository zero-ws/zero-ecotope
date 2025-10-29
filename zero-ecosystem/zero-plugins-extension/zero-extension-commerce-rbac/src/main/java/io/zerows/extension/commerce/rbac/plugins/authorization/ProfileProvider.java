package io.zerows.extension.commerce.rbac.plugins.authorization;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ProfileProvider implements AuthorizationProvider {

    private transient final SecurityMeta aegis;

    private ProfileProvider(final SecurityMeta aegis) {
        this.aegis = aegis;
    }

    public static AuthorizationProvider provider(final SecurityMeta aegis) {
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
        //        final Method method = this.aegis.getAuthorizer().getAuthorization();
        //        Fn.jvmAt(() -> {
        //            /*
        //             * Future<Set<String>> fetching
        //             */
        //            final Future<JsonObject> future = (Future<JsonObject>) method.invoke(this.aegis.getProxy(), user);
        //            future.onComplete(res -> {
        //                if (res.succeeded()) {
        //                    final ConcurrentMap<String, Set<String>> profiles = new ConcurrentHashMap<>();
        //                    Ut.<JsonArray>itJObject(res.result(), (values, field) -> profiles.put(field, Ut.toSet(values)));
        //                    final Authorization required = ProfileAuthorization.create(profiles);
        //                    user.authorizations().put(this.getId(), required);
        //                    handler.handle(Future.succeededFuture());
        //                } else {
        //                    final Throwable ex = res.cause();
        //                    handler.handle(Future.failedFuture(ex));
        //                }
        //            });
        //        });
    }
}
