package io.zerows.plugins.security.weco;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.AsyncUserAtBase;

public abstract class WeComAsyncUserAt extends AsyncUserAtBase {

    public WeComAsyncUserAt() {
        super(TypeLogin.ID_WECOM);
    }

    @Override
    public Future<Boolean> isMatched(final LoginRequest request, final UserAt userAt) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
