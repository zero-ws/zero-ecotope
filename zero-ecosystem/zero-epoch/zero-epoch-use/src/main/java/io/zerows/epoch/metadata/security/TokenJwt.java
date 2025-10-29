package io.zerows.epoch.metadata.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.sdk.security.Token;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TokenJwt implements Token {
    private static final Cc<Integer, Token> USER_TOKEN = Cc.open();

    private final String token;
    private final JsonObject tokenJson;
    private final Credentials credentials;
    private String userKey;

    private TokenJwt(final String token) {
        this.token = token;
        this.tokenJson = this.tokenJson();
        this.credentials = new TokenCredentials(this.token);
    }

    private TokenJwt(final JsonObject tokenJson) {
        this.token = Token.encodeJwt(tokenJson);
        this.tokenJson = tokenJson;
        this.credentials = new TokenCredentials(this.token);
    }

    /**
     * 此处 {@link User#principal()} 的数据结构如下
     * <pre>
     *     {
     *         "user": "唯一用户标识",
     *         "accessToken": "JWT字符串",
     *     }
     * </pre>
     *
     * @param user {@link User} 用户信息
     */
    private TokenJwt(final User user) {
        final JsonObject principle = user.principal();
        this.token = principle.getString(KName.ACCESS_TOKEN);
        this.credentials = new TokenCredentials(this.token);
        this.tokenJson = this.tokenJson();
        this.userKey = principle.getString(KName.USER);
    }

    public static <T> Token of(final T input) {
        Objects.requireNonNull(input);
        return USER_TOKEN.pick(() -> ofInternal(input), input.hashCode());
    }

    private static <T> Token ofInternal(final T input) {
        return switch (input) {
            case final String token -> new TokenJwt(token);
            case final JsonObject tokenJson -> new TokenJwt(tokenJson);
            case final User user -> new TokenJwt(user);
            case null, default -> throw new _60050Exception501NotSupport(TokenJwt.class);
        };
    }

    private JsonObject tokenJson() {
        Objects.requireNonNull(this.token);
        return Token.decodeJwt(this.token);
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

    @Override
    public Credentials credentials() {
        return this.credentials;
    }
}
