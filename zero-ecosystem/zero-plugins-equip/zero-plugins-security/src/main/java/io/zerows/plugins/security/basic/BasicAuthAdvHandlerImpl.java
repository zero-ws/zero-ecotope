package io.zerows.plugins.security.basic;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.audit.Marker;
import io.vertx.ext.auth.audit.SecurityAudit;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.HTTPAuthorizationHandler;
import io.vertx.ext.web.impl.RoutingContextInternal;
import io.vertx.ext.web.impl.Utils;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <pre>
 * 🟢 增强版 Basic 认证处理器实现
 *
 * 1. 🌐 组件定位：
 *    - 实现 `BasicAuthAdvHandler` 接口。
 *    - 继承 `HTTPAuthorizationHandler` 以利用 Vert.x 内置机制。
 *
 * 2. 🎯 主要功能：
 *    - 支持标准 Basic Auth ( RFC7617 )。
 *    - 扩展支持 Bearer Token ( AES 加密令牌 )。
 *    - 自动根据 Header 前缀分发认证逻辑。
 * </pre>
 */
@Slf4j
class BasicAuthAdvHandlerImpl extends HTTPAuthorizationHandler<AuthenticationProvider> implements BasicAuthAdvHandler {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("权限认证失败，提供有效令牌！");
    private static final WebException BAD_REQUEST = new _400BadRequestException("错误的认证请求头格式！");

    BasicAuthAdvHandlerImpl(final AuthenticationProvider authProvider, final String realm) {
        super(authProvider, Type.BASIC, realm);
    }

    /**
     * <pre>
     * 🟢 核心认证分流逻辑
     *
     * 1. 🌐 使用场景：
     *    作为 HTTP 请求的认证入口，拦截请求头 `Authorization`。
     *    根据前缀 Schema 分发到不同的认证策略。
     *
     * 2. 🎯 作用：
     *    - 增强标准 Basic Auth，扩展支持 Bearer Token。
     *    - 统一处理 `Basic` (用户名/密码) 和 `Bearer` (AES 令牌) 两种模式。
     *
     * 3. ⚙️ 处理逻辑：
     *    - 获取 Header -> 检查 Schema -> 分发处理 -> 生成凭证 -> 执行认证。
     * </pre>
     *
     * @param context RoutingContext 路由上下文
     * @return Future<User> 异步认证结果
     */
    @Override
    public Future<User> authenticate(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            return Future.failedFuture(UNAUTHORIZED);
        }
        try {
            final int idx = authorization.indexOf(' ');

            if (idx <= 0) {
                return Future.failedFuture(BAD_REQUEST);
            }

            final String schema = authorization.substring(0, idx);
            if (!this.isValid(schema)) {
                return Future.failedFuture(UNAUTHORIZED);
            }

            final String header = authorization.substring(idx + 1);

            Future<Credentials> futureCred = Future.succeededFuture();
            if (Type.BASIC.is(schema)) {
                // Basic 认证方式
                futureCred = this.createCredentialBasic(header);
            }
            if (Type.BEARER.is(schema)) {
                // AES Token 认证方式
                futureCred = this.createCredentialAes(header);
            }

            if (Objects.isNull(futureCred)) {
                return Future.failedFuture(UNAUTHORIZED);
            }
            return futureCred.compose(credentials -> {
                if (Objects.isNull(credentials)) {
                    return Future.failedFuture(UNAUTHORIZED);
                }
                final SecurityAudit audit = ((RoutingContextInternal) context).securityAudit();
                audit.credentials(credentials);
                return this.authProvider.authenticate(credentials)
                    .andThen(result -> audit.audit(Marker.AUTHENTICATION, result.succeeded()))
                    .recover(err -> Future.failedFuture(new HttpException(401, err)));
            });
        } catch (final RuntimeException e) {
            return Future.failedFuture(e);
        }
    }


    /**
     * <pre>
     * 🟢 解析 Bearer 令牌凭证 (AES)
     *
     * 1. 🌐 使用场景：
     *    当请求头 schema 为 `Bearer` 时调用此方法。
     *    处理 "Bearer <token>" 格式的凭证生成。
     *
     * 2. 🎯 作用：
     *    - 解密 AES 令牌获取 UserID。
     *    - 查找用户会话 (UserSession) 并提取登录用户 (MSUser)。
     *    - 将用户信息转换为 Vert.x 标准凭证 (UsernamePasswordCredentials)。
     *
     * 3. ⚙️ 注意事项：
     *    - 使用 Virtual Thread 执行可能阻塞的 I/O 查询。
     * </pre>
     *
     * @param header 待提取的 Bearer Token 字符串部分
     * @return Future<Credentials> 异步生成的凭证对象
     */
    private Future<Credentials> createCredentialAes(final String header) {
        // AES Token 认证方式
        final TokenBuilder builder = TokenBuilderManager.of().getOrCreate(TokenType.AES);
        final String userId = builder.accessOf(header);
        if (Objects.isNull(userId)) {
            return Future.failedFuture(UNAUTHORIZED);
        }
        return Ux.waitVirtual(() -> {
            final UserAt userAt = UserSession.of().find(userId);
            if (Objects.isNull(userAt)) {
                return null;
            }
            return userAt.logged();
        }).map(user -> {
            if (Objects.isNull(user)) {
                return null;
            }
            // 临时密码占位符
            return new UsernamePasswordCredentials(user.getUsername(), user.getPassword());
        });
    }

    /**
     * <pre>
     * 🟢 解析 Basic 基础凭证
     *
     * 1. 🌐 使用场景：
     *    当请求头 schema 为 `Basic` 时调用此方法。
     *    处理标准的 HTTP Basic Auth 格式。
     *
     * 2. 🎯 作用：
     *    - Base64 解码 Header 内容。
     *    - 解析 "username:password" 格式。
     *    - 生成标准的 UsernamePasswordCredentials。
     * </pre>
     *
     * @param header Base64 编码的凭证字符串
     * @return Future<Credentials> 异步生成的凭证对象
     */
    private Future<Credentials> createCredentialBasic(final String header) {
        final String suser;
        final String spass;

        try {
            // decode the payload
            final String decoded = new String(Utils.base64Decode(header), StandardCharsets.UTF_8);

            final int colonIdx = decoded.indexOf(":");
            if (colonIdx != -1) {
                suser = decoded.substring(0, colonIdx);
                spass = decoded.substring(colonIdx + 1);
            } else {
                suser = decoded;
                spass = null;
            }
        } catch (final RuntimeException e) {
            return null;
        }

        return Future.succeededFuture(new UsernamePasswordCredentials(suser, spass));
    }

    /**
     * <pre>
     * 🟢 验证认证 Schema 是否合法
     *
     * 1. ⚙️ 逻辑：
     *    - 检查是否为 `Basic` (不区分大小写)。
     *    - 检查是否为 `Bearer` (不区分大小写)。
     * </pre>
     *
     * @param schema 请求头中的认证方案前缀
     * @return boolean 是否支持该方案
     */
    private boolean isValid(final String schema) {
        return this.type.is(schema) || Type.BEARER.is(schema);
    }
}
