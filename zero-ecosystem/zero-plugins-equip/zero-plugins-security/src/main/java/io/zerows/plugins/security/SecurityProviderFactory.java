package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 构造核心的 Provider / Handler 的链式结构
 * <pre>
 *   1. {@link AuthenticationProvider} 的构造主要是服务于 WebSocket 中的认证，由于Stomp 协议的特殊性，必须绑定
 *      {@link AuthenticationProvider} 才能执行认证处理，而不能直接调用 Handler
 *   2. 而 {@link AuthenticationHandler} 和 {@link AuthenticationHandler} 是路由需要的东西
 * </pre>
 *
 * @author lang : 2025-10-29
 */
@Slf4j
class SecurityProviderFactory {
    private static final Cc<String, SecurityProviderFactory> CC_CHAIN = Cc.openThread();
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.openThread();
    private static final Cc<String, AuthenticationHandler> CC_HANDLER_401 = Cc.openThread();
    private static final AtomicBoolean IS_PROVIDER = new AtomicBoolean(Boolean.TRUE);
    private final Vertx vertxRef;

    private SecurityProviderFactory(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    static SecurityProviderFactory of(final Vertx vertxRef) {
        final String cacheKey = Objects.toString(vertxRef.hashCode());
        return CC_CHAIN.pick(() -> new SecurityProviderFactory(vertxRef), cacheKey);
    }

    /**
     * 1 x N 认证模型
     * <pre>
     *     1. 1 x 内置认证器（保证所有 {@link SecurityMeta} 中定义的 Type 是一致的
     *     2. N x 自定义认证器（保证所有 @Wall 中定义的认证方法都能被调用到）
     * </pre>
     * 部分 Schema 中的 Provider 可以为空，但最终提供合并之后的 Provider 必须要一致，这样可以让 Provider 的创建
     * 更加有意义，简单说 Handler 的创建必须依赖 Provider 来完成
     *
     * @param metaSet 安全元数据集合
     * @return 认证提供器
     */
    AuthenticationProvider providerOfAuthentication(final Set<SecurityMeta> metaSet) {
        final AuthenticationHandlerChain providerSet = this.providerOfCombine(metaSet);
        return providerSet.providerAll();
    }

    private AuthenticationHandlerChain providerOfCombine(final Set<SecurityMeta> metaSet) {
        final AuthenticationHandlerChain providerSet = new AuthenticationHandlerChain();
        if (Objects.isNull(metaSet) || metaSet.isEmpty()) {
            return providerSet;
        }
        // 401 Provider 任意一个通过认证即可，复杂模式，Jwt / Basic 同时启用时非常有效
        final ChainAuth chainAuth = ChainAuth.any();
        metaSet.forEach(meta -> {
            final AuthenticationProvider provider = AuthenticationNative.createProvider(this.vertxRef, meta);
            if (Objects.nonNull(provider)) {
                chainAuth.add(provider);
            }
        });
        // 如果是 Basic 等，此处可能为空，为空则不加入链中，但后续有自定义认证器，所以创建 Handler 是无忧的
        providerSet.addOfVertx(chainAuth);
        // 自定义 401 Provider / 访问 @Wall 中的认证方法
        metaSet.stream().map(meta -> CC_PROVIDER_401.pick(
            () -> new AuthenticationProviderOne(this.vertxRef, meta), meta.id(this.vertxRef))
        ).forEach(providerSet::addOfExtension);
        if (IS_PROVIDER.getAndSet(Boolean.FALSE)) {
            log.info("[ PLUG ] ( Secure ) Provider 数量：{}", providerSet.size());
        }
        return providerSet;
    }


    WallHandler handlerOfAuthentication(final Set<SecurityMeta> metaSet) {
        // 提取前置验证器
        final WallHandler chain = new AuthenticationHandlerWall();
        if (metaSet.isEmpty()) {
            return null;
        }
        // 先构造 Provider
        final AuthenticationHandlerChain securitySet = this.providerOfCombine(metaSet);
        // 提取第一个内置创建 Handler
        final SecurityMeta metaFirst = metaSet.iterator().next();
        final AuthenticationHandler handler = AuthenticationNative
            .createHandler(this.vertxRef, metaFirst, securitySet.providerOne());
        if (Objects.nonNull(handler)) {
            chain.add(handler);
        }

        // 构造自定义的 Handler
        final List<SecurityMeta> sortedList = new ArrayList<>(metaSet);
        Collections.sort(sortedList);
        sortedList.stream().map(meta -> CC_HANDLER_401.pick(
            () -> new AuthenticationHandlerOne(securitySet.providerAll(), meta),
            meta.id(this.vertxRef)
        )).forEach(chain::add);
        return chain;
    }

    /**
     * 授权专用 Handler 构造器，授权 403 的构造器比 401 的简单，就看选择，要使用就用，不使用就直接跳过
     * <pre>
     *     1. 403 对应的构造器不存在 1 x N 的链式结构，只是单纯的 N 选 1 的执行。
     *     2. 若使用原生的授权构造器，则直接调用内置构造器即可，若不使用内置构造器，则使用 @Wall 中定义的授权方法
     * </pre>
     *
     * @param metaSet 安全元数据集合
     * @return 授权处理器
     */
    AuthorizationHandler handlerOfAuthorization(final Set<SecurityMeta> metaSet) {
        if (metaSet.isEmpty()) {
            // 为空则不加载任何 Handler
            return null;
        }

        return AuthorizationHandlerOne.create(metaSet);
    }
}
