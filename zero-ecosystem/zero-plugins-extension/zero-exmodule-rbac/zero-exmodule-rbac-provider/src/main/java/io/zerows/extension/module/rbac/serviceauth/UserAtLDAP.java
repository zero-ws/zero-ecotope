package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.service.AsyncUserAtBase;
import jakarta.inject.Inject;

@SPID("UserAt/LDAP")
public class UserAtLDAP extends AsyncUserAtBase {
    @Inject
    private UserAuthStub userAuthStub;

    public UserAtLDAP() {
        super(TypeLogin.LDAP);
    }

    @Override
    protected Future<UserAt> findUser(final String ldap) {
        return this.userAuthStub.whereBy(ldap, TypeLogin.LDAP)
            .compose(this::userAtEphemeral);
    }
}
