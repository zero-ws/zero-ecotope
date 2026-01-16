package io.zerows.plugins.security.jwt;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
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
    public Future<JsonObject> response() {
        return null;
    }


    @Override
    public String getToken(final UserAt userAt) {
        return MANAGER.getOrCreate(TokenType.JWT).accessOf(userAt);
    }

    @Override
    public String getRefreshToken(final UserAt userAt) {
        return MANAGER.getOrCreate(TokenType.JWT).refreshOf(userAt);
    }
}
