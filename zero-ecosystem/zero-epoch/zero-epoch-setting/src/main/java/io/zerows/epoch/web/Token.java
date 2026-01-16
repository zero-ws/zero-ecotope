package io.zerows.epoch.web;

import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.spi.HPI;

/**
 * 和 {@link TokenBuilder} 不同的接口，作为通用接口用来处理
 * <pre>
 *     1. {@link JsonObject} 转换 {@link String}
 *     2. {@link String} 转换 {@link JsonObject}
 * </pre>
 */
public interface Token {
    Cc<String, Token> CC_TOKEN = Cc.open();

    static Token of(final TokenType type) {
        return CC_TOKEN.pick(() -> HPI.findOne(Token.class, "Token/" + type.name()), type.name());
    }

    JsonObject decode(String token);

    String encode(JsonObject payload);
}
