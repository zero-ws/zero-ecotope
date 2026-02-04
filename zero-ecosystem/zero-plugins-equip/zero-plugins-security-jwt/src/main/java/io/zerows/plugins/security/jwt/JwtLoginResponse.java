package io.zerows.plugins.security.jwt;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.webflow.Akka;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.service.AsyncLoginResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtLoginResponse extends AsyncLoginResponse {
    private static final TokenBuilderManager MANAGER = TokenBuilderManager.of();
    private String tokenType = "Bearer";
    private long expiresIn;

    public JwtLoginResponse(final UserAt userAt) {
        super(userAt);
        // 提取配置信息
        final SecurityConfig config = SecurityActor.configJwt();
        final String expires = config.option("expiredAt", "2h");
        final Duration duration = R2MO.toDuration(expires);
        this.expiresIn = duration.getSeconds();
    }


    @Override
    public Future<JsonObject> replyToken(final String token, final String refreshToken) {
        final JsonObject response = new JsonObject();
        response.put("tokenType", this.tokenType);
        response.put("expiresIn", this.expiresIn);
        response.put(KName.TOKEN, token);
        response.put("refreshToken", refreshToken);
        return Future.succeededFuture(response);
    }

    @Override
    public Akka<String> getTokenAsync() {
        return MANAGER.getOrCreate(TokenType.JWT).accessOf(this.userAt);
    }

    @Override
    public Akka<String> getTokenRefresh() {
        return MANAGER.getOrCreate(TokenType.JWT).refreshOf(this.userAt);
    }
}
