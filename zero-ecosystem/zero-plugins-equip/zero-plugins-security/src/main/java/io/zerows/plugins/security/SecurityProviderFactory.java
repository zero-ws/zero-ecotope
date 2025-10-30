package io.zerows.plugins.security;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.ChainAuth;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.ChainAuthHandler;
import io.zerows.cosmic.plugins.security.exception._40080Exception500PreAuthentication;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
class SecurityProviderFactory {
    private static final Cc<String, SecurityProviderFactory> CC_CHAIN = Cc.openThread();
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.openThread();
    private static final Cc<String, AuthenticationHandler> CC_HANDLER_401 = Cc.openThread();
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
     *
     * @param metaSet 安全元数据集合
     *
     * @return 认证提供器
     */
    AuthenticationProvider providerOfAuthentication(final Set<SecurityMeta> metaSet) {
        final ChainAuth chain = ChainAuth.all();
        if (Objects.isNull(metaSet) || metaSet.isEmpty()) {
            return chain;
        }
        // 筛选一个出来做 401 Provider
        final SecurityMeta metaFirst = metaSet.iterator().next();
        final AuthenticationProvider provider =
            AuthenticationNative.createProvider(this.vertxRef, metaFirst);
        // 如果是 Basic 等，此处可能为空，为空则不加入链中，但后续有自定义认证器，所以创建 Handler 是无忧的
        if (Objects.nonNull(provider)) {
            chain.add(provider);
        }
        // 自定义 401 Provider / 访问 @Wall 中的认证方法
        metaSet.stream().map(meta -> CC_PROVIDER_401.pick(
            () -> new AuthenticationCommonProvider(this.vertxRef, meta), meta.id(this.vertxRef))
        ).forEach(chain::add);
        return chain;
    }


    ChainAuthHandler handlerOfAuthentication(final Set<SecurityMeta> metaSet) {
        // 前置 Handler 验证
        this.ensurePreHandler(metaSet);
        // 提取前置验证器
        final ChainAuthHandler chain = ChainAuthHandler.all();
        if (metaSet.isEmpty()) {
            return chain;
        }
        // 先构造 Provider
        final AuthenticationProvider provider = this.providerOfAuthentication(metaSet);
        // 提取第一个内置创建 Handler
        final SecurityMeta metaFirst = metaSet.iterator().next();
        AuthenticationHandler handler =
            AuthenticationNative.createHandler(this.vertxRef, metaFirst);
        if (Objects.isNull(handler) && SecurityType.BASIC == metaFirst.getType()) {
            handler = BasicAuthHandler.create(provider);
        }
        if (Objects.nonNull(handler)) {
            chain.add(handler);
        }


        // 构造自定义的 Handler
        final List<SecurityMeta> sortedList = new ArrayList<>(metaSet);
        Collections.sort(sortedList);
        sortedList.stream().map(meta -> CC_HANDLER_401.pick(
            () -> new AuthenticationCommonHandler(provider, meta),
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
     *
     * @return 授权处理器
     */
    AuthorizationHandler handlerOfAuthorization(final Set<SecurityMeta> metaSet) {

        return null;
    }

    /**
     * 检查程序，此处检查程序对整个矩阵有所要求
     * <pre>
     *     1. 同一个路径下的 Pre 内置 Provider 只允许有一个，目前版本不可以混用
     *     2. 先做内置认证，然后排序执行 {@link Wall} 中定义的多维认证
     *     3. 多维认证过程中如果有任何一个认证失败，则直接返回失败
     * </pre>
     *
     * @param metaSet 安全元数据集合
     */
    private void ensurePreHandler(final Set<SecurityMeta> metaSet) {
        final Set<String> preSet = new HashSet<>();
        final Set<String> pathSet = new HashSet<>();
        metaSet.forEach(meta -> {
            preSet.add(meta.idPre(this.vertxRef));
            pathSet.add(meta.getPath());
        });
        final String path = pathSet.iterator().next();
        Fn.jvmKo(1 != preSet.size(), _40080Exception500PreAuthentication.class, preSet.size(), path);
    }
}
