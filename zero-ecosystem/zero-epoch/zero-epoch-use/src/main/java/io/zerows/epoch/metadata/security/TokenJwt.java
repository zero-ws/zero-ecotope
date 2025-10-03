package io.zerows.epoch.metadata.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.EmSecure;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.support.Ut;
import io.zerows.sdk.security.Lee;
import io.zerows.sdk.security.LeeBuiltIn;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TokenJwt implements WebToken {
    private static final Cc<String, JsonObject> STORE_TOKEN = Cc.open();
    private static final Cc<Integer, WebToken> USER_TOKEN = Cc.open();

    private String token;
    private JsonObject tokenJson;
    private String userKey;

    private TokenJwt(final String token) {
        this.token = token;
        this.tokenJson = this.tokenJson();
    }

    private TokenJwt(final JsonObject tokenJson) {
        final Lee lee = Ut.service(LeeBuiltIn.class);
        this.token = lee.encode(tokenJson, AegisItem.configMap(EmSecure.AuthWall.JWT));
        this.tokenJson = tokenJson;
    }

    private TokenJwt(final User user) {
        final JsonObject principle = user.principal();
        if (principle.containsKey(KName.USER)) {
            this.userKey = principle.getString(KName.USER);
            if (principle.containsKey(KName.ACCESS_TOKEN)) {
                this.token = principle.getString(KName.ACCESS_TOKEN);
                this.tokenJson = this.tokenJson();
            }
        } else {
            // 避免从 JWT 中反复提取用户信息
            this.token = principle.getString(KName.ACCESS_TOKEN);
            this.tokenJson = this.tokenJson();
            this.userKey = principle.getString(KName.USER);
        }
    }

    public static <T> WebToken of(final T input) {
        Objects.requireNonNull(input);
        return USER_TOKEN.pick(() -> ofInternal(input), input.hashCode());
    }

    private static <T> WebToken ofInternal(final T input) {
        if (input instanceof final String token) {
            return new TokenJwt(token);
        } else if (input instanceof final JsonObject tokenJson) {
            return new TokenJwt(tokenJson);
        } else if (input instanceof final User user) {
            return new TokenJwt(user);
        } else {
            throw new _60050Exception501NotSupport(TokenJwt.class);
        }
    }

    private JsonObject tokenJson() {
        Objects.requireNonNull(this.token);
        final Lee lee = Ut.service(LeeBuiltIn.class);
        return STORE_TOKEN
            // 防止 JWT 的高频解码（速度很慢）
            .pick(() -> lee.decode(this.token, AegisItem.configMap(EmSecure.AuthWall.JWT)), this.token);
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public String authorization() {
        return "Bearer " + this.token;
    }

    @Override
    public String user() {
        return this.userKey;
    }

    @Override
    public JsonObject data() {
        return this.tokenJson;
    }
}
