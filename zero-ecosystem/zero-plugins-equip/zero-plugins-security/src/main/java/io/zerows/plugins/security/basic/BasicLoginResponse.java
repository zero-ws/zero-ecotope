package io.zerows.plugins.security.basic;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.security.service.AsyncLoginResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BasicLoginResponse extends AsyncLoginResponse {
    private String username;

    public BasicLoginResponse(final UserAt userAt) {
        super(userAt);
        final MSUser user = userAt.logged();
        this.username = user.getUsername();
    }

    @Override
    public Future<JsonObject> response() {
        final JsonObject response = new JsonObject();
        response.put(KName.ID, this.getId());
        response.put(KName.TOKEN, this.getToken());
        response.put(KName.USERNAME, this.username);
        return Future.succeededFuture(response);
    }

    @Override
    public String getToken(final UserAt user) {
        // 该方法已被覆盖，不会调用父类方法
        return TokenBuilderManager.of().getOrCreate(TokenType.AES).accessOf(user);
    }
}
