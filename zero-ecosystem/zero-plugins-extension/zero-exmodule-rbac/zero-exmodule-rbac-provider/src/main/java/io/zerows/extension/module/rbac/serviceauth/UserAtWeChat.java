package io.zerows.extension.module.rbac.serviceauth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.extension.module.rbac.servicespec.UserAuthStub;
import io.zerows.plugins.security.weco.WeChatAsyncUserAt;
import jakarta.inject.Inject;

/**
 * @author lang : 2025-12-11
 */
@SPID("UserAt/ID_WECHAT")
public class UserAtWeChat extends WeChatAsyncUserAt {
    @Inject
    private UserAuthStub userStub;

    @Override
    public Future<UserAt> findUser(final String unionId) {
        return this.userStub.whereBy(unionId, TypeLogin.ID_WECHAT)
            // 构造 UserAt 对象
            .compose(this::userAtEphemeral);
    }
}
