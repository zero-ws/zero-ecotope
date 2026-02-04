package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.webflow.Akka;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
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
    public Future<JsonObject> replyToken(final String token, final String refreshToken) {
        final JsonObject response = new JsonObject();
        response.put(KName.ID, this.getId());
        response.put(KName.TOKEN, token);
        response.put(KName.USERNAME, this.username);
        return Future.succeededFuture(response);
    }

    @Override
    public Akka<String> getTokenAsync() {
        return TokenBuilderManager.of().getOrCreate(TokenType.AES).accessOf(this.userAt);
    }
}
