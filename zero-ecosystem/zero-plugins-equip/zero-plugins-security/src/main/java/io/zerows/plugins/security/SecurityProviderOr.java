package io.zerows.plugins.security;

import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

import java.util.Objects;

public class SecurityProviderOr {
    private final ChainAuth authAll;
    private final ChainAuth authAny;
    private AuthenticationProvider baseProvider;

    SecurityProviderOr() {
        this.authAll = ChainAuth.all();
        this.authAny = ChainAuth.any();
    }

    public void addOfVertx(final AuthenticationProvider provider) {
        if (Objects.isNull(provider)) {
            return;
        }
        this.baseProvider = provider;
        this.addOfExtension(provider);
    }

    public void addOfExtension(final AuthenticationProvider provider) {
        if (Objects.isNull(provider)) {
            return;
        }
        this.authAll.add(provider);
        this.authAny.add(provider);
    }

    public AuthenticationProvider providerOne(final boolean ifAny) {
        if (Objects.isNull(this.baseProvider)) {
            return ifAny ? this.providerAny() : this.providerAll();
        }
        return this.baseProvider;
    }

    public AuthenticationProvider providerAll() {
        return this.authAll;
    }

    public AuthenticationProvider providerAny() {
        return this.authAny;
    }
}
