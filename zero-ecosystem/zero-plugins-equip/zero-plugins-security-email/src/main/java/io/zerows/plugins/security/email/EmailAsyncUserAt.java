package io.zerows.plugins.security.email;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.AsyncUserAtBase;

public abstract class EmailAsyncUserAt extends AsyncUserAtBase {
    public EmailAsyncUserAt() {
        super(TypeLogin.EMAIL);
    }

    @Override
    public Future<Boolean> isMatched(final LoginRequest request, final UserAt userAt) {
        final EmailAuthConfig config = EmailAuthActor.configOf();
        return this.isMatched(request, userAt, config.expiredAt());
    }
}
