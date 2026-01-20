package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.common.Kv;

import java.util.Objects;

public class TokenBuilderAES extends TokenBuilderBase {
    private final TokenAESGenerator generator;
    private final TokenAESRefresher refresher;

    public TokenBuilderAES() {
        this.generator = new TokenAESGenerator();
        this.refresher = new TokenAESRefresher();
    }

    @Override
    public String accessOf(final UserAt userAt) {
        final MSUser logged = this.ensureAuthorized(userAt);
        return this.generator.tokenGenerate(userAt.id().toString(), logged.token());
    }

    /**
     * ✅ [Optimized] Single-pass decryption and validation
     */
    @Override
    public String accessOf(final String token) {
        // 原来的写法是先 tokenValidate (解密1次) 再 tokenSubject (解密2次)
        // 现在直接调用 validateAndExtract，只解密1次
        final Kv<String, TokenType> kv = this.tokenOf(token);
        return Objects.isNull(kv) ? null : kv.key();
    }

    @Override
    public Kv<String, TokenType> tokenOf(final String token) {
        return this.generator.validateAndExtract(token);
    }

    @Override
    public String refreshOf(final UserAt userAt) {
        return this.refresher.tokenGenerate(userAt.id().toString());
    }
}
