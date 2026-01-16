package io.zerows.plugins.security.service;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;

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

    @Override
    public String accessOf(final String token) {
        if (!this.generator.tokenValidate(token)) {
            return null;
        }
        return this.generator.tokenSubject(token);
    }

    @Override
    public String refreshOf(final UserAt userAt) {
        return this.refresher.tokenGenerate(userAt.id().toString());
    }
}
