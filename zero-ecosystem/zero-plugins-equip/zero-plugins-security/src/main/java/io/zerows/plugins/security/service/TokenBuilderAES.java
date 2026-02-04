package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.vertx.common.cache.AkkaOr;
import io.vertx.core.Future;

import java.util.Objects;

public class TokenBuilderAES extends TokenBuilderBase {
    private final TokenAESGenerator generator;
    private final TokenAESRefresher refresher;

    public TokenBuilderAES() {
        this.generator = new TokenAESGenerator();
        this.refresher = new TokenAESRefresher();
    }

    @Override
    public Akka<String> accessOf(final UserAt userAt) {
        final MSUser logged = this.ensureAuthorized(userAt);
        final Future<String> generated = Future.succeededFuture(this.generator.tokenGenerate(userAt.id().toString(), logged.token()));
        return AkkaOr.of(generated);
    }

    /**
     * ✅ [Optimized] Single-pass decryption and validation
     */
    @Override
    public Akka<String> accessOf(final String token) {
        // 原来的写法是先 tokenValidate (解密1次) 再 tokenSubject (解密2次)
        // 现在直接调用 validateAndExtract，只解密1次
        final Kv<String, TokenType> validated = this.generator.validateAndExtract(token);
        if (Objects.isNull(validated)) {
            return AkkaOr.of();
        }
        return AkkaOr.of(Future.succeededFuture(validated.key()));
    }

    @Override
    public Akka<Kv<String, TokenType>> tokenOf(final String token) {
        final Kv<String, TokenType> validated = this.generator.validateAndExtract(token);
        return AkkaOr.of(Future.succeededFuture(validated));
    }

    @Override
    public Akka<String> refreshOf(final UserAt userAt) {
        return AkkaOr.of(this.refresher.tokenGenerate(userAt.id().toString()));
    }
}
