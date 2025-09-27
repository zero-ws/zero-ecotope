package io.zerows.module.security.atom.token;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.util.Ut;
import io.zerows.module.security.atom.AegisItem;
import io.zerows.module.security.zdk.Lee;
import io.zerows.module.security.zdk.LeeBuiltIn;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class JwtToken implements WebToken {
    private static final Cc<String, JsonObject> STORE_TOKEN = Cc.open();
    private static final Cc<Integer, WebToken> USER_TOKEN = Cc.open();

    private String token;
    private JsonObject tokenJson;
    private String userKey;

    private JwtToken(final String token) {
        this.token = token;
        this.tokenJson = this.tokenJson();
    }

    private JwtToken(final JsonObject tokenJson) {
        final Lee lee = Ut.service(LeeBuiltIn.class);
        this.token = lee.encode(tokenJson, AegisItem.configMap(EmSecure.AuthWall.JWT));
        this.tokenJson = tokenJson;
    }

    private JwtToken(final User user) {
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
            return new JwtToken(token);
        } else if (input instanceof final JsonObject tokenJson) {
            return new JwtToken(tokenJson);
        } else if (input instanceof final User user) {
            return new JwtToken(user);
        } else {
            throw new _501NotSupportException(JwtToken.class);
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
