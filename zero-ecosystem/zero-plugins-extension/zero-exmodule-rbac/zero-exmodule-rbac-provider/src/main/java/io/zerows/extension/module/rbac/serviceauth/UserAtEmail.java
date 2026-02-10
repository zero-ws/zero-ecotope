package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.zerows.extension.module.rbac.metadata.logged.ScUser;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.email.EmailAsyncUserAt;
import jakarta.inject.Inject;

@SPID("UserAt/EMAIL")
public class UserAtEmail extends EmailAsyncUserAt {

    @Inject
    private UserAuthStub userAuthStub;

    @Override
    protected Future<UserAt> findUser(final String email) {
        return this.userAuthStub.whereBy(email, TypeLogin.EMAIL)
            .compose(this::userAtEphemeral);
    }

    @Override
    public Future<User> loadAuthorization(final User logged) {
        return ScUser.initProfile(logged).map(v -> logged);
    }
}
