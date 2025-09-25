package io.zerows.extension.commerce.rbac.osgi.spi.secure;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.atom.ScToken;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;
import io.zerows.extension.runtime.skeleton.osgi.spi.environment.Permit;

/**
 * @author lang : 2023-09-15
 */
public class SafePermit implements Permit {
    private final ScClock<ScToken> cache;

    public SafePermit() {
        this.cache = ClockFactory.ofToken(this.getClass());
    }

    @Override
    public Future<Boolean> token(final String token) {
        // 处理令牌相关信息
        if (Ut.isNil(token)) {
            return Ux.futureF();
        }
        // 拆分出来的 token 信息
        final JsonObject extract = Ux.Jwt.extract(token);

        // token / user
        final String user = extract.getString(KName.USER);

        // 验证 accessToken 是否存在
        return this.cache.get(token, false)

            // 验证 Token
            .compose(scToken -> this.cache.verify(scToken, token, user));
    }
}
