package io.zerows.plugins.security.basic;

import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenBuilder;
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
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.HTTPAuthorizationHandler;
import io.vertx.ext.web.impl.RoutingContextInternal;
import io.vertx.ext.web.impl.Utils;

import java.nio.charset.StandardCharsets;

class BasicAuthAdvHandlerImpl extends HTTPAuthorizationHandler<AuthenticationProvider> implements BasicAuthAdvHandler {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("权限认证失败，提供有效令牌！");
    private static final WebException BAD_REQUEST = new _400BadRequestException("错误的认证请求头格式！");

    BasicAuthAdvHandlerImpl(final AuthenticationProvider authProvider, final String realm) {
        super(authProvider, Type.BASIC, realm);
    }

    /**
     * <pre>
     * 认证分流核心逻辑 🔀
     *
     * 本方法是对原始 Basic Auth 的增强，增加了对 Bearer Token 的支持。
     * 根据 Authorization 请求头的前缀 schema 进行不同策略的分发：
     *
     * 1. Basic Schema 🔑
     *    传统的用户名/密码认证，需配合 Base64 解码。
     *    调用 {@link #authenticateTokenBasic} 处理。
     *
     * 2. Bearer Schema 🎫
     *    使用 AES 加密的令牌 (Token) 认证。
     *    调用 {@link #authenticateTokenBearer} 处理。
     *
     * 注意：请求头格式必须满足 "Schema Token" 的标准规范。
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
            if (Type.BASIC.is(schema)) {
                return this.authenticateTokenBasic(context, header);
            }
            if (Type.BEARER.is(schema)) {
                // AES Token 认证方式
                return this.authenticateTokenBearer(context, header);
            }
            return Future.failedFuture(BAD_REQUEST);
        } catch (final RuntimeException e) {
            return Future.failedFuture(e);
        }
    }

    /**
     * <pre>
     * Bearer Token 认证处理器 (AES) 🔐
     *
     * 针对 "Bearer <token>" 格式的请求头进行处理。
     * 这里的 Token 采用 AES 算法加密，解密后验证其有效性。
     *
     * ⚙️ 处理流程：
     * 1. 令牌提取：从 Authorization 头中解析出 Bearer 后的内容。
     * 2. 令牌解析：使用 {@link TokenBuilder} (AES) 解析令牌。
     * 3. 会话查找：调用 {@link UserSession#find(String)} 验证令牌并获取用户详情。
     * 4. 虚拟线程：由于涉及 I/O 或 复杂计算，部分逻辑在 Virtual Thread 中执行。
     * </pre>
     *
     * @param context RoutingContext 上下文 (暂未使用)
     * @param header  待验证的 Token 字符串
     * @return Future<User> 认证成功的用户对象
     */
    private Future<User> authenticateTokenBearer(final RoutingContext context, final String header) {
        // AES Token 认证方式
        final TokenCredentials credentials = new TokenCredentials(header);
        return this.authenticateToken(context, credentials);
    }

    private Future<User> authenticateTokenBasic(final RoutingContext context, final String header) {
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
            return Future.failedFuture(new HttpException(400, e));
        }

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(suser, spass);
        return this.authenticateToken(context, credentials);
    }

    private Future<User> authenticateToken(final RoutingContext context, final Credentials credentials) {
        final SecurityAudit audit = ((RoutingContextInternal) context).securityAudit();
        audit.credentials(credentials);
        return this.authProvider.authenticate(credentials)
            .andThen(result -> audit.audit(Marker.AUTHENTICATION, result.succeeded()))
            .recover(err -> Future.failedFuture(new HttpException(401, err)));
    }

    private boolean isValid(final String schema) {
        return this.type.is(schema) || Type.BEARER.is(schema);
    }
}
