package io.zerows.extension.module.rbac.serviceauth;


import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.service.AsyncUserAtBase;
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
    public Future<User> loadAuthorization(final User logged) {
        return ScUser.initProfile(logged).map(v -> logged);
    }
}
