package io.zerows.extension.commerce.rbac.agent.service.login.jwt;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.extension.commerce.rbac.atom.ScToken;
import io.zerows.extension.commerce.rbac.uca.logged.ScUser;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;

/*
 * Jwt WebToken Service for:
 * 1) Stored token information into jwt token
 * 2) Verify token based on stored access_token in database.
 */
public class JwtService implements JwtStub {
    private final ScClock<ScToken> cache;

    public JwtService() {
        this.cache = ClockFactory.ofToken(this.getClass());
    }

    /**
     * 存储 Jwt Token 的核心逻辑，访问 {@link ScClock}，数据结构参考 {@see ScClockToken} 中的定义
     * 方法中的备注信息。
     *
     * @param data 需要存储在令牌中的信息
     *
     * @return 存储的最终结果
     */
    @Override
    public Future<JsonObject> store(final JsonObject data) {
        /*
         * - clientId
         * - authToken ( Token )
         * - authRefresh ( Refresh Token )
         * - iat
         * - exp
         */
        final ScToken token = this.cache.generate(data);
        /*
         * 用户登录初始化缓存相关信息，登录成功之后，生成 Token 信息
         */
        return ScUser.login(data)
            /*
             * id           = scToken
             * token        = scToken
             * refreshToken = scToken
             */
            .compose(logged -> this.cache.put(token.id(), token, token.token()))
            .compose(logged -> Ux.future(token.authResponse()));
    }

    @Override
    public Future<Boolean> verify(final String userKey, final String token) {
        /*
         * 三合一的方式提取 ScToken
         */
        return this.cache.get(token, false)
            // 验证 Token
            .compose(scToken -> this.cache.verify(scToken, token, userKey));
    }
}
