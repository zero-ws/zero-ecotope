package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallProvider;

import java.util.Set;

/**
 * <pre>
 * 🟢 安全墙提供者
 *
 * 1. 🌐 全局说明
 *    安全墙（Security Wall）是 Zero 框架中用于保护 HTTP 接口的核心组件。
 *    本类作为 `WallExecutor` 的上层提供者，负责根据元数据构建认证与授权组件。
 *
 * 2. 🧬 核心逻辑：多墙并在
 *    - 一个路径（Path）是唯一的，例如 `/api/*`。
 *    - 一个路径可以关联多个 `SecurityMeta` 定义（Security Wall）。
 *    - 每个 `SecurityMeta` 对应一种安全机制（如 BASIC, JWT, OAUTH2）。
 *    - 示例场景：
 *      - `/api/*` -> 同时支持 BASIC (内部调用) 和 JWT (前端调用)。
 *      - `/oauth/*` -> 同时支持 JWT (API) 和 OAUTH2 (三方登录)。
 *
 * 3. 🔧 编排方式
 *    - 每个 `SecurityMeta` 都会绑定一个具体的 `WallExecutor` 执行器。
 *    - 如果同一个 Path 命中多个 SecurityMeta，系统将根据 Chain 模式或
 *      Composite 模式进行编排（具体由 Factory 实现）。
 * </pre>
 *
 * @author lang : 2025-10-29
 */
public class SecurityWallProvider implements WallProvider {
    /**
     * <pre>
     * 🟢 构建认证提供者 (401 处理核心)
     *
     * 1. 🌐 使用场景
     *    系统启动时，根据路由定义的 `Set<SecurityMeta>` 集合，
     *    构建用于执行身份验证（Authentication）的底层 Provider。
     *
     * 2. 🎯 作用
     *    - 聚合多个 SecurityMeta 的定义。
     *    - 生成对应的 Vert.x `AuthenticationProvider`。
     *    - 它是校验用户身份（"你是谁"）的逻辑入口。
     * </pre>
     *
     * @param vertxRef Vert.x 实例引用
     * @param metaSet  当前 Path 下绑定的所有安全元数据集合
     * @return 构造好的认证提供者
     */
    @Override
    public AuthenticationProvider providerOfAuthentication(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).providerOfAuthentication(metaSet);
    }

    /**
     * <pre>
     * 🟢 构建认证处理器 (401 拦截器)
     *
     * 1. 🌐 使用场景
     *    在 Vert.x Web 路由中挂载的 Handler，用于拦截未认证请求。
     *    如果认证失败，此处理器将负责抛出 `401 Unauthorized` 异常或重定向。
     *
     * 2. 🎯 作用
     *    - 解析 HTTP 请求中的凭证（Header/Cookie/Param）。
     *    - 调用 `AuthenticationProvider` 执行校验。
     *    - 决定请求是继续放行（Context.next）还是中断（fail）。
     * </pre>
     *
     * @param vertxRef Vert.x 实例引用
     * @param metaSet  当前 Path 下绑定的所有安全元数据集合
     * @return 构造好的认证处理器
     */
    @Override
    public AuthenticationHandler handlerOfAuthentication(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).handlerOfAuthentication(metaSet);
    }

    /**
     * <pre>
     * 🟢 构建授权处理器 (403 拦截器)
     *
     * 1. 🌐 使用场景
     *    在用户通过认证（401）后，进一步检查用户是否有权限访问当前资源。
     *    如果权限不足，此处理器将负责抛出 `403 Forbidden` 异常。
     *
     * 2. 🎯 作用
     *    - 检查 User Principal 中的权限/角色信息。
     *    - 对比 `SecurityMeta` 中定义的所需权限。
     *    - 确保用户不仅是"合法的"，而且是"被允许的"。
     * </pre>
     *
     * @param vertxRef Vert.x 实例引用
     * @param metaSet  当前 Path 下绑定的所有安全元数据集合
     * @return 构造好的授权处理器
     */
    @Override
    public AuthorizationHandler handlerOfAuthorization(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).handlerOfAuthorization(metaSet);
    }
}
