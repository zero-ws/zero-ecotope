package io.zerows.extension.module.rbac.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.rbac.component.ScClock;
import io.zerows.extension.module.rbac.component.ScClockFactory;
import io.zerows.extension.module.rbac.metadata.ScToken;
import io.zerows.extension.skeleton.spi.ScPermit;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author lang : 2023-09-15
 */
public class ScPermitAdmin implements ScPermit {
    private final ScClock<ScToken> cache;

    public ScPermitAdmin() {
        this.cache = ScClockFactory.ofToken(this.getClass());
    }

    @Override
    public Future<Boolean> token(final String token) {
        // 处理令牌相关信息
        if (Ut.isNil(token)) {
            return Ux.futureF();
        }
        // 拆分出来的 token 信息
        final JsonObject extract = Account.userToken(token);

        // token / user
        final String user = extract.getString(KName.USER);

        // 验证 accessToken 是否存在
        return this.cache.get(token, false)

            // 验证 Token
            .compose(scToken -> this.cache.verify(scToken, token, user));
    }
}
