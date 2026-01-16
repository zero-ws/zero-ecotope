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
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 🟢 安全模块工厂的核心实现
 *
 * 1. 🌐 核心策略 ( 关键修复 )：
 *    - Handler 之间保持 OR 关系 ( fail-over 机制 )。
 *    - Provider 内部保持 OR 关系 ( ChainAuth.any )。
 *    - 目的：确保 Basic + AES 模式能在原生 Basic 校验失败后，被自定义 Provider 挽救。
 *
 * 2. 🎯 主要职责：
 *    - 编排 AuthenticationHandler ( 认证 )。
 *    - 编排 AuthorizationHandler ( 授权 )。
 *    - 管理 Provider 的聚合与生命周期。
 * </pre>
 *
 * @author lang : 2025-10-29
 */
@Slf4j
class SecurityProviderFactory {

    private static final Cc<String, SecurityProviderFactory> CC_FACTORY = Cc.openThread();
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER = Cc.openThread();
    private static final Cc<String, AuthenticationHandler> CC_HANDLER = Cc.openThread();
    // ✅ 新增：全局静态原子集合，用于跨线程日志去重
    private static final Set<String> LOGGED_TYPES = ConcurrentHashMap.newKeySet();

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
     * 🟢 核心编排入口：认证墙构建
     *
     * 1. 🌐 使用场景：
     *    容器启动时，根据配置元数据 ( `SecurityMeta` ) 构建安全拦截墙。
     *
     * 2. ⚙️ 执行逻辑：
     *    - 按顺序处理每种安全定义。
     *    - 为每个定义构建 "原生 + 自定义" 的混合 Provider。
     *    - 将 Handler 注册到 WallHandler ( 支持 Fail-Over )。
     *    - 挂载 Finalizer 进行凭证转换 ( User -> Account )。
     * </pre>
     *
     * @param metaSet 安全配置元数据集合
     * @return WallHandler 认证处理器墙
     */
    WallHandler handlerOfAuthentication(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }

        // 1. 创建墙 (Iterate Logic)
        final AuthenticationHandlerWall wall = new AuthenticationHandlerWall();

        // 2. 排序
        final List<SecurityMeta> sortedList = new ArrayList<>(metaSet);
        Collections.sort(sortedList);

        // 3. 编排循环
        for (final SecurityMeta meta : sortedList) {
            // A. 创建复合 Provider (必须是 ANY，不能是 ALL)
            final AuthenticationProvider provider = this.providerComposite(meta);

            // B. 创建原生 Handler (绑定上面的 Provider)
            final AuthenticationHandler handler = this.handlerNative(meta, provider);

            // C. 加入墙
            if (Objects.nonNull(handler)) {
                // ✅ 核心修改：原子控制日志
                // 只有该类型第一次被加载时，才会输出日志
                final String typeKey = String.valueOf(meta.getType());
                if (LOGGED_TYPES.add(typeKey)) {
                    log.info("[ PLUG ] ( Security ) Loaded: Type={}, Handler={}", typeKey, handler.getClass().getSimpleName());
                }
                wall.add(handler);
            }
        }

        // 4. 挂载 Finalizer (严出)
        // 这一步是必须的，用于将 User 转换为 Account
        if (!sortedList.isEmpty()) {
            // 给 Finalizer 一个全量的 Provider 集合以防万一
            final AuthenticationProvider allProvider = this.providerOfAuthentication(metaSet);
            // 使用第一个 Meta 作为上下文
            final SecurityMeta mainMeta = sortedList.getFirst();

            final AuthenticationHandlerOne finalizer = new AuthenticationHandlerOne(allProvider, mainMeta);
            wall.withFinalizer(finalizer);
        }

        return wall;
    }

    /**
     * <pre>
     * 🟢 复合 Provider 构建 ( 关键逻辑 )
     *
     * 1. 🎯 目的：
     *    构建一个能够同时处理 "原生 Vert.x 逻辑" 和 "自定义业务逻辑" 的 Provider 链。
     *
     * 2. ⚠️ 关键点：
     *    必须使用 `ChainAuth.any()`！
     *    - 如果使用 `all()`，当请求是 AES Token 但 Schema 是 Basic 时，
     *      原生 Basic Provider 会因为解不出 username/password 而直接报错，导致后续流程中断。
     *    - 使用 `any()` 可让原生组件失败后，继续尝试自定义的 AES 解密逻辑。
     * </pre>
     *
     * @param meta 安全配置元数据
     * @return AuthenticationProvider 复合认证提供者
     */
    private AuthenticationProvider providerComposite(final SecurityMeta meta) {
        return CC_PROVIDER.pick(() -> {
            // ⚠️ 绝对关键：必须使用 any()。
            // 如果使用 all()，Basic AES 请求会被原生 Basic Provider 拦截并报错，导致 Custom Provider 无法执行。
            final ChainAuth chain = ChainAuth.any();

            // 1. 原生 Provider (Vert.x 自带校验)
            final AuthenticationProvider nativeProvider = AuthenticationNative.createProvider(this.vertxRef, meta);
            if (Objects.nonNull(nativeProvider)) {
                chain.add(nativeProvider);
            }

            // 2. 自定义 Provider (R2MO 业务校验/AES解密)
            // 无论原生是否存在，都挂载自定义 Provider 作为兜底或主逻辑
            chain.add(new AuthenticationProviderOne(this.vertxRef, meta));

            return chain;
        }, meta.id(this.vertxRef));
    }

    /**
     * <pre>
     * 🟢 处理器 Handler 构建
     *
     * 1. ⚙️ 逻辑：
     *    - 优先尝试创建 Vert.x 原生 Handler ( 如 BasicAuthHandler, JWTAuthHandler )。
     *    - 如果原生不支持 ( 如自定义类型 )，则降级使用 `AuthenticationHandlerOne`。
     *    - 确保所有 Handler 都绑定了能够处理业务逻辑的 Provider。
     * </pre>
     *
     * @param meta     安全配置元数据
     * @param provider 对应的认证提供者
     * @return AuthenticationHandler 认证处理器实例
     */
    private AuthenticationHandler handlerNative(final SecurityMeta meta, final AuthenticationProvider provider) {
        if (provider == null) {
            return null;
        }
        return CC_HANDLER.pick(() -> {
            // 尝试创建原生 Handler (Basic/JWT/Digest)
            final AuthenticationHandler handler = AuthenticationNative.createHandler(this.vertxRef, meta, provider);
            if (handler != null) {
                return handler;
            }
            // 如果不是原生类型，创建自定义 HandlerOne 作为入口
            return new AuthenticationHandlerOne(provider, meta);
        }, meta.id(this.vertxRef));
    }

    /**
     * <pre>
     * 🟢 WebSocket 聚合 Provider
     *
     * 1. 🌐 使用场景：
     *    为 WebSocket 或其他非标准 HTTP 场景提供一个聚合的认证接口。
     *
     * 2. 🎯 作用：
     *    - 将所有配置的 Provider 扁平化聚合到一个 Chain 中。
     *    - 用于全局性的 token 验证或其他通用认证需求。
     * </pre>
     *
     * @param metaSet 安全配置元数据集合
     * @return AuthenticationProvider 聚合后的认证提供者
     */
    AuthenticationProvider providerOfAuthentication(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }
        final ChainAuth chain = ChainAuth.any();
        // 简单聚合所有
        metaSet.forEach(meta -> {
            final ChainAuth subChain = (ChainAuth) this.providerComposite(meta);
            chain.add(subChain);
        });
        return chain;
    }

    /**
     * <pre>
     * 🟢 授权处理器构建
     *
     * 1. 🎯 作用：
     *    基于角色的访问控制 ( RBAC ) 处理器。
     *    目前使用 `AuthorizationHandlerOne` 作为统一实现。
     * </pre>
     *
     * @param metaSet 安全配置元数据集合
     * @return AuthorizationHandler 授权处理器
     */
    AuthorizationHandler handlerOfAuthorization(final Set<SecurityMeta> metaSet) {
        if (metaSet == null || metaSet.isEmpty()) {
            return null;
        }
        return AuthorizationHandlerOne.create(metaSet);
    }
}