package io.zerows.plugins.security.jwt;

import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.web.handler.HttpException;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.*;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * <pre>
 * 🟢 JWT 令牌认证扩展实现
 *
 * 1. 🌐 全局说明
 *    实现了标准 JSON Web Token (JWT) 的认证逻辑。
 *    作为 `AuthenticationHandlerGateway` 的核心策略之一，处理无状态的身份验证。
 *
 * 2. 🎯 核心逻辑
 *    - 支持检测：
 *      调用 `TokenType.fromString` 识别 Authorization 头。
 *      如果是 `Bearer` 且格式符合 JWT (3部分，2个点号)，则判定为支持。
 *    - 解析与认证：
 *      1. 提取 Token 字符串。
 *      2. 执行基本的格式校验（段数、字符合法性）。
 *      3. 获取对应的 `AuthenticationProvider` (基于 Vert.x Auth JWT)。
 *      4. 执行签名校验和有效期检查。
 *
 * 3. 🔄 结果模式
 *    - 成功：返回 "已认证" 状态 (User) 的 `ExtensionAuthenticationResult`。
 *      JWT 自包含用户信息，验证通过后直接生成 User 对象。
 *    - 失败：抛出 401 异常。
 * </pre>
 */
@Slf4j
public class JwtExtensionAuthentication implements ExtensionAuthentication {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("JWT 权限认证失败，提供有效令牌！");
    private final SecurityProvider provider;

    /**
     * <pre>
     * 🟢 构造函数
     *
     * 初始化 JWT 扩展组件，并绑定到 `WALL_JWT` 类型的安全提供者工厂。
     * </pre>
     */
    public JwtExtensionAuthentication() {
        this.provider = SecurityProvider.of(SecurityConstant.WALL_JWT);
    }

    /**
     * <pre>
     * 🟢 绑定安全墙类型
     *
     * @return {@link SecurityConstant#WALL_JWT}
     * </pre>
     */
    @Override
    public String name() {
        return SecurityConstant.WALL_JWT;
    }

    /**
     * <pre>
     * 🟢 策略支持判断
     *
     * 利用 {@link TokenType} 工具类进行智能嗅探。
     * 仅当 Token 类型明确被识别为 {@link TokenType#JWT} 时返回 true。
     * </pre>
     *
     * @param authorization HTTP Authorization Header
     * @return true if token is JWT
     */
    @Override
    public boolean support(final String authorization) {
        final TokenType token = TokenType.fromString(authorization);
        return TokenType.JWT == token;
    }

    /**
     * <pre>
     * 🟢 执行 JWT 认证流程
     *
     * 1. 🛡️ 格式预校验
     *    - 验证 Token 字符集（字母、数字、-、_、.）。
     *    - 验证段数（必须包含 3 段，即 2 个点号）。
     *
     * 2. 🔑 签名验证
     *    - 通过 `SecurityProvider` 获取配置好的 AuthProvider。
     *    - 调用 `provider.authenticate(tokenCredentials)`。
     *    - 这一步会校验签名 (Signature) 和过期时间 (exp)。
     *
     * 3. 🏁 结果封装
     *    - 认证成功后，直接返回包含 User 的 Result。
     *    - 进入 "已认证" 状态，Gateway 将跳过后续步骤。
     * </pre>
     *
     * @param input 包含 Authorization 头的输入数据
     * @param vertx Vert.x 实例
     * @param meta  安全元数据配置
     * @return 异步结果，包含已认证的 User 对象
     */
    @Override
    public Future<ExtensionAuthenticationResult> resolve(final JsonObject input, final Vertx vertx, final SecurityMeta meta) {
        // Authorization 请求头提取
        final String authorization = Ut.valueString(input, HttpHeaders.AUTHORIZATION.toString());
        try {

            final int idx = authorization.indexOf(' ');
            final String token = authorization.substring(idx + 1);
            // Handler 内置调用
            final SecurityConfig config = SecurityActor.configOf(meta.getType());
            int segments = 0;
            for (int i = 0; i < token.length(); i++) {
                final char c = token.charAt(i);
                if (c == '.') {
                    if (++segments == 3) {
                        return Future.failedFuture(new HttpException(400, "Too many segments in token"));
                    }
                    continue;
                }
                if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                    continue;
                }
                // invalid character
                return Future.failedFuture(new HttpException(400, "Invalid character in token: " + (int) c));
            }

            final TokenCredentials credentials = new TokenCredentials(token);
            final AuthenticationProvider provider = this.provider.configureProvider401(vertx, config);
            return provider.authenticate(credentials).map(user -> {
                if (Objects.isNull(user)) {
                    throw UNAUTHORIZED;
                }
                return ExtensionAuthenticationResult.bindAsync(user);
            });
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            return Future.failedFuture(UNAUTHORIZED);
        }
    }
}
