package io.zerows.extension.module.rbac.serviceauth;


import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.skeleton.spi.ExLog;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.service.AsyncUserAtBase;
import io.zerows.spi.HPI;
import jakarta.inject.Inject;

@SPID("UserAt/PASSWORD")
public class UserAtPassword extends AsyncUserAtBase {
    @Inject
    private UserAuthStub userAuthStub;

    public UserAtPassword() {
        super(TypeLogin.PASSWORD);
    }

    @Override
    protected Future<UserAt> findUser(final String username) {
        return this.userAuthStub.whereUsername(username)
            .compose(this::userAtEphemeral);
    }

    @Override
    public Future<UserAt> loadLogged(final LoginRequest request) {
        return super.loadLogged(request)
            .compose(userAt -> this.logged(request.getId(), userAt));
    }

    private Future<UserAt> logged(final String username, final UserAt userAt) {
        final JsonObject data = new JsonObject()
            .put("logAgent", "rbac.login")
            .put("logUser", userAt.logged().getId().toString())
            .put("infoReadable", "用户登录：" + username)
            .put("infoSystem", "User login succeeded: " + username)
            .put("metadata", new JsonObject()
                .put("username", username)
                .put("type", TypeLogin.PASSWORD.name()));
        return HPI.of(ExLog.class).waitAsync(
                logger -> logger.system(data).otherwise(nil -> null)
            )
            .map(nil -> userAt);
    }
}
