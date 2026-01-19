package io.zerows.plugins.security.sms;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.AsyncUserAtBase;

public abstract class SmsAsyncUserAt extends AsyncUserAtBase {
    public SmsAsyncUserAt() {
        super(TypeLogin.SMS);
    }

    @Override
    public Future<Boolean> isMatched(final LoginRequest request, final UserAt userAt) {
        final SmsAuthConfig config = SmsAuthActor.configOf();
        return this.isMatched(request, userAt, config.expiredAt());
    }
}
