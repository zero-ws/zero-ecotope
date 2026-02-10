package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.audit.Marker;
import io.vertx.ext.auth.audit.SecurityAudit;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.vertx.ext.web.impl.RoutingContextInternal;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.exception._80246Exception404ExtensionMiss;
import io.zerows.plugins.security.exception._80247Exception400AuthorizationFormat;
import io.zerows.plugins.security.service.AsyncSession;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 * 🟢 认证网关处理器
 *
 * 1. 🌐 全局说明
 *    Zero Security 的核心分流组件，实现了 Vert.x `AuthenticationHandler` 接口。
 *    作为 HTTP 请求进入安全系统的第一道关卡，负责解析 `Authorization` 请求头，
 *    并将其路由到具体的认证策略（Extension）中。
 *
 * 2. 🧬 核心逻辑：三合一分流
 *    本处理器通过 SPI 机制加载 `ExtensionAuthentication` 实现，支持多种认证协议共存。
 *    - 🛡️ BEARER 令牌：
 *       - JWT (JSON Web Token)：标准令牌认证。
 *       - AES (Symmetric Encryption)：对称加密令牌认证。
 *    - 🛡️ BASIC 认证：
 *       - 用户名/密码的标准 HTTP 基础认证。
 *    - 🛡️ SPI 扩展：
 *       - 第三方或自定义认证协议（如 OAuth2, API Key 等）。
 *
 * 3. 🔧 工作流程
 *    1. 📥 拦截请求：提取 HTTP 头部的 `Authorization` 字段。
 *    2. 🔍 策略匹配：遍历所有注册的 `ExtensionAuthentication`，调用 `support()` 寻找处理者。
 *    3. 🚀 执行解析：调用匹配组件的 `resolve()` 方法，尝试提取凭证或直接认证用户。
 *    4. ⚖️ 结果裁决：
 *       - ✅ 已认证 (Verified)：Extension 返回了完整的 User 对象（如 JWT 解析成功），直接通过。
 *       - ⚠️ 待验证 (Credentials)：Extension 返回了 Credentials（如 Basic 如果只解析了账号密码），
 *          则转交底层 `AuthenticationProvider` 进行密码比对。
 *    5. 🚫 失败处理：若无匹配策略或解析失败，返回 401 Unauthorized。
 * </pre>
 *
 * @author lang
 */
@Slf4j
class AuthenticationHandlerGateway extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private final ConcurrentMap<String, Set<ExtensionAuthentication>> extensionMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, SecurityMeta> mapMeta = new ConcurrentHashMap<>();

    /**
     * <pre>
     * 🟢 构造函数：初始化网关
     *
     * 1. ⚙️ 初始化过程
     *    - 绑定 Vert.x 实例与 AuthProvider。
     *    - 构建 SecurityMeta 映射表 (Type -> Meta)。
     *    - 加载 SPI 扩展：扫描 classpath 下所有 `ExtensionAuthentication` 实现。
     *
     * 2. 🛡️ 兜底策略
     *    - 强制注册 Default Security (Basic & AES) 到 `WALL_BASIC`。
     *    - 确保即使没有 SPI 扩展，系统也能处理基础认证。
     * </pre>
     * 特殊数据结构
     * <pre>
     *     JWT   -> {@see JwtExtensionAuthentication}
     *     BASIC -> {@link ExtensionAuthenticationBasic} / {@link ExtensionAuthenticationAES}
     * </pre>
     *
     * @param provider 底层认证提供者 (Zero Security Guard)
     * @param metaSet  当前路径绑定的所有安全元数据
     */
    AuthenticationHandlerGateway(final AuthenticationProvider provider, final Set<SecurityMeta> metaSet) {
        super(provider);
        metaSet.forEach(meta -> this.mapMeta.put(meta.getType(), meta));
        // SPI 扩展加载
        final List<ExtensionAuthentication> extensions = HPI.findMany(ExtensionAuthentication.class);
        extensions.forEach(extension ->
            this.extensionMap.computeIfAbsent(extension.name(), k -> new HashSet<>()).add(extension));
        // 默认兜底的一次性策略：Basic，同时支持 AES
        this.extensionMap.put(SecurityConstant.WALL_BASIC, Set.of(
            new ExtensionAuthenticationBasic(),
            new ExtensionAuthenticationAES()
        ));
    }

    /**
     * <pre>
     * 🟢 核心认证流程
     *
     * 1. 🔍 嗅探 (Sniffing)
     *    - 提取 HTTP `Authorization` 头。
     *    - 遍历已注册的 Extension，寻找支持该 Authorization 格式的组件。
     *    - 这里的匹配逻辑是 "First Win" (第一个支持的胜出)。
     *
     * 2. 🧩 解析 (Resolution)
     *    - 调用组件的 `resolve` 方法，将 Header 解析为中间结果 (`ExtensionAuthenticationResult`)。
     *
     * 3. ⚖️ 裁决 (Verdict)
     *    - 分支 A (Direct User): 如果 Result 包含 User，说明认证已在上一步完成 (如 JWT 验签)。
     *      -> 将 User 注入 RoutingContext。
     *      -> 调用 `provider.authenticate(null)` 触发生命周期回调但跳过校验。
     *
     *    - 分支 B (Deferred Auth): 如果 Result 仅包含 Credentials (如 Basic/AES)。
     *      -> 将 Credentials 传递给 `provider.authenticate(creds)`。
     *      -> 由底层的 Realm/Store 进行真实的密码校验或令牌查找。
     * </pre>
     * 此处网关的工作流程
     * <pre>
     *     1. 作为请求接受第一门卫，提取 Authorization 头。
     *     2. 遍历所有注册的 ExtensionAuthentication 实现，调用 support() 方法寻找匹配者。
     *     3. 找到匹配的 Extension 后，调用其 resolve() 方法尝试解析凭证。
     *     4. 根据解析结果分两种情况处理：
     *        - 如果解析结果包含已验证的 User 对象，直接通过认证。
     *        - 如果解析结果仅包含 Credentials，则传递给底层的 AuthenticationProvider 进行验证。
     *     5. 如果没有找到匹配的 Extension，或解析失败，返回 401 Unauthorized 错误。
     *     6. 执行完 Provider 的 authenticate() 方法后，调用对应的 AuthenticationBackendHandler 进行最终的用户上下文设置。
     * </pre>
     *
     * @param context RoutingContext 路由上下文
     */
    @Override
    public Future<User> authenticate(final RoutingContext context) {

        final HttpServerRequest request = context.request();
        final String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            return Future.failedFuture(SecurityConstant.UNAUTHORIZED);
        }
        try {
            final int idx = authorization.indexOf(' ');
            if (idx <= 0) {
                return Future.failedFuture(new _80247Exception400AuthorizationFormat(authorization));
            }


            final ExtensionAuthentication found = this.extensionMap.values().stream()
                .flatMap(Collection::stream)
                .filter(extension -> this.mapMeta.containsKey(extension.name()))
                .filter(extension -> extension.support(authorization))
                .findFirst()
                .orElse(null);
            if (Objects.isNull(found)) {
                log.error("[ PLUG ] ( Security ) 未装配合法的 Extension 组件：{}", authorization);
                return Future.failedFuture(new _80246Exception404ExtensionMiss(authorization));
            }


            // 参数构造
            final JsonObject params = new JsonObject();
            {
                params.put(HttpHeaders.AUTHORIZATION.toString(), authorization);    // 目前只需要此部分数据
                final Session session = context.session();
                params.put(KName.SESSION, session.id());
            }
            final SecurityMeta meta = this.mapMeta.get(found.name());

            final Vertx vertx = context.vertx();
            // 此处执行原生解析流程，内置 Provider 执行
            return found.resolve(params, vertx, meta)
                .compose(authResult -> this.authenticate(context, authResult))
                .recover(err -> Future.failedFuture(new HttpException(401, err)))
                .compose(verified -> AuthenticationBackendHandler.of(this.authProvider, meta).authenticate(context));
        } catch (final Throwable ex) {
            return Future.failedFuture(ex);
        }
    }

    private Future<User> authenticate(final RoutingContext context,
                                      final AsyncSession asyncSession) {
        final SecurityAudit audit = ((RoutingContextInternal) context).securityAudit();

        audit.credentials(asyncSession);
        // 根据结果进行分流处理
        return this.authProvider.authenticate(asyncSession).map(verified -> {
            final SecuritySession session = SecuritySession.of();
            return session.authorizedUser(context, verified);
        }).andThen(result -> audit.audit(Marker.AUTHENTICATION, result.succeeded()));
    }
}
