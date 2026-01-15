package io.zerows.plugins.security;

import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

import java.util.Objects;

/**
 * 复杂架构中，此处一定要使用 all 的操作，简单说就是所有的 Provider 都要执行
 * 认证操作，任何一个认证失败都会导致最终的认证失败，而不是 Any 放行，放行会导致最终的 Chain 发生断裂
 */
public class SecurityProviderOr {
    private final ChainAuth authAll;
    private AuthenticationProvider baseProvider;

    SecurityProviderOr() {
        this.authAll = ChainAuth.all();
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
    }

    public AuthenticationProvider providerOne() {
        if (Objects.isNull(this.baseProvider)) {
            return this.providerAll();
        }
        return this.baseProvider;
    }

    public AuthenticationProvider providerAll() {
        return this.authAll;
    }
}
