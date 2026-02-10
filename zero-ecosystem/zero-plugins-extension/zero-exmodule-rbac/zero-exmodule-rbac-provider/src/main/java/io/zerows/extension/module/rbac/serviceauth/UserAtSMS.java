package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.sms.SmsAsyncUserAt;
import jakarta.inject.Inject;

@SPID("UserAt/SMS")
public class UserAtSMS extends SmsAsyncUserAt {
    @Inject
    private UserAuthStub userAuthStub;

    @Override
    protected Future<UserAt> findUser(final String mobile) {
        return this.userAuthStub.whereBy(mobile, TypeLogin.SMS)
            .compose(this::userAtEphemeral);
    }
}
