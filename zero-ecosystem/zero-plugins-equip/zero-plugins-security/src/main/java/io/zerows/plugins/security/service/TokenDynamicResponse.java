package io.zerows.plugins.security.service;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.webflow.Akka;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.metadata.YmSecurity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class TokenDynamicResponse extends AsyncLoginResponse {

    private static YmSecurity CONFIG;

    public TokenDynamicResponse(final UserAt userAt) {
        super(userAt);
        CONFIG = SecurityActor.configuration();
    }

    @Override
    public Future<JsonObject> replyToken(final String token, final String refreshToken) {
        final JsonObject response = new JsonObject();
        response.put(KName.ID, this.getId().toString());
        response.put(KName.TOKEN, token);
        response.put("refreshToken", refreshToken);
        return Future.succeededFuture(response);
    }

    private TokenType determineTokenType() {
        // 根据配置决定使用哪种 Token 类型
        if (Objects.isNull(CONFIG)) {
            CONFIG = SecurityActor.configuration();
        }
        final YmSecurity.Limit limit = CONFIG.getLimit();
        if (Objects.isNull(limit)) {
            return TokenType.JWT;
        }
        return limit.getTokenType();
    }

    @Override
    public Akka<String> getTokenAsync() {
        // token
        return TokenBuilderManager.of().getOrCreate(this.determineTokenType()).accessOf(this.userAt);
    }

    @Override
    public Akka<String> getTokenRefresh() {
        // refreshToken
        return TokenBuilderManager.of().getOrCreate(this.determineTokenType()).refreshOf(this.userAt);
    }
}
