package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * <pre>
 * 🟢 安全模块工厂的核心实现 ( Final Edition )
 *
 * 1. 🏗️ 架构拓扑：
 * - Handler 层：多条并行的安检通道 (Basic/AES 通道, JWT 通道...)。
 * - Provider 层：每条通道内部是 "双重验证" (ChainAuth.all)。
 * └─ Step 1: Native Provider (验签/验密/伪装通过)
 * └─ Step 2: One Provider    (查库/查缓存/补充校验)
 *
 * 2. 🎯 核心逻辑：
 * 不论请求走哪条通道 (AES 还是 JWT)，原生校验通过后，
 * 都会强制执行 {@link AuthenticationBackendProvider} 进行业务补位。
 *
 * 3. 🏁 最终收口：
 * 所有 Handler 执行完毕后，由 {@link AuthenticationBackendHandler} 统一进行 User -> Account 转换。
 * </pre>
 *
 * @author lang : 2025-10-29
 */
@Slf4j
class SecurityProviderFactory {

    private static final Cc<String, SecurityProviderFactory> CC_FACTORY = Cc.openThread();
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER = Cc.openThread();
    private static final Cc<String, AuthenticationHandlerGateway> CC_HANDLER = Cc.openThread();

    private final Vertx vertxRef;

    private SecurityProviderFactory(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    public static SecurityProviderFactory of(final Vertx vertxRef) {
        final String cacheKey = Objects.toString(vertxRef.hashCode());
        return CC_FACTORY.pick(() -> new SecurityProviderFactory(vertxRef), cacheKey);
    }

    /**
     * <pre>
     * 🟢 核心编排入口
     *    1. 先构造 Provider 链
     *       JWT 和 OAuth 内置，所以这两种只要包含了 Handler 内部就有了
     * </pre>
     */
    AuthenticationHandler handlerOfAuthentication(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }
        // 统一的 Provider
        final AuthenticationProvider provider = this.providerOfAuthentication(metaSet);
        // 构造 Gateway Handler
        return CC_HANDLER.pick(
            () -> new AuthenticationHandlerGateway(provider, metaSet),
            String.valueOf(System.identityHashCode(metaSet))
        );
    }

    /**
     * <pre>
     *  🟢 聚合 Provider / 构造多条并行通道中复合型的 Provider
     *     1. 先做 {@link SecurityMeta} 的过滤，只有特殊 WallType 才会进入对应的 Provider 逻辑
     *     2. 除开 JWT 和 OAuth2，这两种内置的 Provider 会被自动加入 Handler，其他的都会被过滤掉
     *     3. Provider x N + One 的结构搭建
     * </pre>
     */
    AuthenticationProvider providerOfAuthentication(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }
        // 先构造原生态的 SecurityMeta Provider 链 (OR 关系)
        return CC_PROVIDER.pick(() -> {
            final ChainAuth chain = ChainAuth.any();
            metaSet.stream()
                .map(meta -> new AuthenticationBackendProvider(this.vertxRef, meta))
                .forEach(chain::add);
            return chain;
        }, String.valueOf(System.identityHashCode(metaSet)));
    }

    /**
     * 🟢 授权处理器构建
     *
     * @param metaSet 安全元信息集合
     * @return 授权处理器
     */
    AuthorizationHandler handlerOfAuthorization(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }
        return AuthorizationHandlerOne.create(metaSet);
    }
}