package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.rbac.component.acl.relation.LinkManager;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.servicespec.UserStub;
import io.zerows.extension.skeleton.spi.ExLog;
import io.zerows.extension.skeleton.spi.ExTrash;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class UserActor {

    @Inject
    private transient UserStub stub;

    @Address(Addr.User.PASSWORD)
    public Future<JsonObject> password(final Envelop envelop) {
        /*
         * Async for user password / modification
         */
        final String userId = envelop.userId();
        final JsonObject params = Ux.getJson(envelop);
        return LinkManager.refPerms().saveAsync(userId, params);
    }

    @Address(Addr.User.PROFILE)
    public Future<JsonObject> profile(final Envelop envelop) {
        final String userId = envelop.userId();
        final JsonObject params = Ux.getJson(envelop);
        return this.stub.updateInformation(userId, params);
    }

    @Address(Addr.Auth.LOGOUT)
    public Future<Boolean> logout(final Envelop envelop) {
        final String token = envelop.token();
        final String habitus = envelop.habitus();
        final String userId = envelop.userId();
        return ScUser.logout(habitus).compose(result -> {
            /*
             * Here we should do
             * 1. Session / ES Purging
             * 2. User clean
             * 3. Fix issue of 4.x
             * 4. Permission Pool / Auth Pool Clean
             */
            final RoutingContext context = envelop.context();
            // configure.clearUser();

            final Session session = context.session();
            if (Objects.nonNull(session) && !session.isDestroyed()) {
                session.destroy();
            }
            return this.loggedOut(userId);
        });
    }

    private Future<Boolean> loggedOut(final String userId) {
        final JsonObject data = new JsonObject()
            .put("logAgent", "rbac.logout")
            .put("logUser", userId)
            .put("infoReadable", "用户注销：" + userId)
            .put("infoSystem", "User logout succeeded: " + userId)
            .put("metadata", new JsonObject().put("userId", userId));
        return HPI.of(ExLog.class).waitAsync(
                logger -> logger.system(data).otherwise(nil -> null)
            )
            .map(nil -> Boolean.TRUE);
    }

    @Address(Addr.User.GET)
    public Future<JsonObject> getById(final String key) {
        return LinkManager.refPerms().fetchAsync(key);
    }

    @Address(Addr.User.ADD)
    public Future<JsonObject> create(final JsonObject data) {
        return this.stub.createUser(data);
    }

    @Address(Addr.User.UPDATE)
    public Future<JsonObject> update(final String key, final JsonObject data) {
        return LinkManager.refPerms().saveAsync(key, data);
    }

    @Address(Addr.User.DELETE)
    public Future<Boolean> delete(final String key) {
        // SPI: ExTrash
        return HPI.of(ExTrash.class).waitOr(
            tunnel -> LinkManager.refPerms().fetchAsync(key)
                .compose(user -> tunnel.backupAsync("sec.user", user))
                .compose(backup -> this.stub.deleteUser(key)),
            () -> this.stub.deleteUser(key)
        );
    }

    // ====================== Information ( By Type ) =======================
    /*
     * User information findRunning from the system to extract data here.
     */
    @Address(Addr.User.INFORMATION)
    public Future<JsonObject> information(final Envelop envelop) {
        final String userId = envelop.userId();
        return this.stub.fetchInformation(userId);
    }

    @Address(Addr.User.QR_USER_SEARCH)
    public Future<JsonObject> searchByType(final String identifier, final JsonObject criteria) {
        return LinkManager.refExtension().searchAsync(identifier, criteria);
    }
}
