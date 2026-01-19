package io.zerows.plugins.security;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.security.service.AsyncSession;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * <pre>
 * 🟢 AES 对称加密令牌认证扩展实现
 *
 * 1. 🌐 全局说明
 *    实现了基于 AES 对称加密的 Bearer Token 认证逻辑。
 *    作为 `AuthenticationHandlerGateway` 的默认支持策略之一，常用于内部服务调用或轻量级认证。
 *
 * 2. 🎯 核心逻辑
 *    - 支持检测：匹配 "Bearer " 前缀的 Authorization 头。
 *    - 解析逻辑：
 *      1. 提取 Token 字符串。
 *      2. 使用 AES 算法解密 Token，获取 User ID。
 *      3. 在 `UserSession` 中查找活跃用户会话。
 *
 * 3. 🔄 Gateway 交互设计 (Re-Authentication Pattern)
 *    本实现采用 "提取凭证" (Credentials Extraction) 模式，而非直接返回 User。
 *    - 步骤 1: `resolve` 方法解密 Token 并找到关联的用户信息 (MSUser)。
 *    - 步骤 2: 将用户信息包装为 `UsernamePasswordCredentials`。
 *    - 步骤 3: 返回 `bindAsync(credentials)` 状态。
 *    - 步骤 4: Gateway 接收到 Credentials 后，调用底层 `AuthenticationProvider`。
 *    - 目的: 复用 AuthProvider 的逻辑（如加载角色、权限、构建完整的 Vert.x User 对象），
 *           确保 Token 认证与账号密码登录产生的 User 对象结构一致。
 * </pre>
 */
@Slf4j
public class ExtensionAuthenticationAES implements ExtensionAuthentication {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("AES 权限认证失败，提供有效令牌！");

    /**
     * <pre>
     * 🟢 绑定安全墙类型
     *
     * 绑定的安全墙名称，通常与 `SecurityMeta` 中的配置对应。
     * 此处绑定到 `WALL_BASIC`，意味着它作为基础认证墙的一种扩展支持（Bearer 形式）。
     * </pre>
     *
     * @return {@link SecurityConstant#WALL_BASIC}
     */
    @Override
    public String name() {
        return SecurityConstant.WALL_BASIC;
    }

    /**
     * <pre>
     * 🟢 策略支持判断
     *
     * 判断当前 Authorization 头是否符合 AES Token 的格式要求。
     * - 前缀必须为 "Bearer " (标准 OAuth2/JWT 格式)。
     * - AES Token 伪装成 Bearer Token 进行传输。
     * </pre>
     *
     * @param authorization HTTP 请求头 Authorization 的值
     * @return true 如果是以 "Bearer " 开头
     */
    @Override
    public boolean support(final String authorization) {
        final TokenType token = TokenType.fromString(authorization);
        return TokenType.AES == token;
    }

    /**
     * <pre>
     * 🟢 执行 AES Token 解析流程
     *
     * 1. 🔍 提取与解密
     *    - 从 "Bearer <token>" 中截取 <token>。
     *    - 调用 `TokenBuilder` (AES实现) 进行解密，还原出 User ID。
     *
     * 2. 🕵️‍♂️ 会话查找
     *    - 使用 User ID 在 `UserSession` 中查找活跃的 `UserAt`。
     *    - 如果 Session 不存在或已过期，认证失败。
     *
     * 3. 🔄 凭证转换 (关键设计)
     *    - 虽然在此处已经拿到了用户对象 (`MSUser`)，但本方法选择**不**直接返回 User。
     *    - 而是提取 username/password 构造 `UsernamePasswordCredentials`。
     *    - 通过 `ExtensionAuthenticationResult.bindAsync(credentials)` 通知 Gateway。
     *    - 这样做是为了让 Gateway 将控制权交给 Application 的 `AuthenticationProvider`，
     *      由它去执行标准的登录流程（包括权限加载、审计等），保证上下文的一致性。
     * </pre>
     *
     * @param input 包含 Authorization 头的输入数据
     * @param vertx Vert.x 实例
     * @param meta  安全元数据配置
     * @return 异步结果，包含待验证的 Credentials
     */
    @Override
    public Future<AsyncSession> resolve(final JsonObject input, final Vertx vertx, final SecurityMeta meta) {
        // Authorization 请求头提取
        final String authorization = Ut.valueString(input, HttpHeaders.AUTHORIZATION.toString());
        try {

            final int idx = authorization.indexOf(' ');
            final String header = authorization.substring(idx + 1);
            // AES Token 认证方式
            final TokenBuilder builder = TokenBuilderManager.of().getOrCreate(TokenType.AES);
            final String userId = builder.accessOf(header);
            if (Objects.isNull(userId)) {
                return Future.failedFuture(UNAUTHORIZED);
            }
            return Ux.waitVirtual(() -> {
                final UserAt userAt = UserSession.of().find(userId);
                /*
                 * 修正逻辑：
                 * 此处如果是 AES 模式，其实已经拿到了 UserAt，这是一个完整的用户会话对象。
                 * 如果想走这里直接返回 User，可以使用 Account.userVx(userAt) 将其转换为 Vert.x User。
                 *
                 * 但为了配合默认的 AuthProvider (SPI) 统一加载行为（如加载角色、权限），
                 * 这里选择 "降级" 为 Credentials，让 AuthProvider 重新 "登录" 一次。
                 *
                 * 注意：这要求 MSUser 中的 password 是 AuthProvider 可识别的（明文或特定哈希）。
                 */
                return Account.userVx(userAt);
            }).map(authorized -> {
                if (Objects.isNull(authorized)) {
                    // 用户不存在或会话丢失
                    throw UNAUTHORIZED;
                }
                return AsyncSession.bindAsync(authorized, authorization);
            });
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            return Future.failedFuture(UNAUTHORIZED);
        }
    }
}
