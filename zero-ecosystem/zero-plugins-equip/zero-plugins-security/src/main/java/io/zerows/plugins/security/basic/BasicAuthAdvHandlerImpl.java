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
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.HTTPAuthorizationHandler;
import io.vertx.ext.web.impl.RoutingContextInternal;
import io.vertx.ext.web.impl.Utils;
import io.zerows.program.Ux;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

class BasicAuthAdvHandlerImpl extends HTTPAuthorizationHandler<AuthenticationProvider> implements BasicAuthAdvHandler {
    private static final WebException UNAUTHORIZED = new _401UnauthorizedException("权限认证失败，提供有效令牌！");
    private static final WebException BAD_REQUEST = new _400BadRequestException("错误的认证请求头格式！");

    BasicAuthAdvHandlerImpl(final AuthenticationProvider authProvider, final String realm) {
        super(authProvider, Type.BASIC, realm);
    }

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

    private Future<User> authenticateTokenBearer(final RoutingContext context, final String header) {
        // AES Token 认证方式
        final TokenBuilder builder = TokenBuilderManager.of().getOrCreate(TokenType.AES);
        final String token = builder.accessOf(header);
        if (Objects.isNull(token)) {
            return Future.failedFuture(UNAUTHORIZED);
        }
        return Ux.waitVirtual(() -> {
            // 提取用户 ID
            final UserAt userAt = UserSession.of().find(token);
            if (Objects.isNull(userAt)) {
                throw UNAUTHORIZED;
            }
            return userAt;
        }).compose(userAt -> {

            return Future.succeededFuture();
        });
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

        final SecurityAudit audit = ((RoutingContextInternal) context).securityAudit();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(suser, spass);
        audit.credentials(credentials);

        return this.authProvider.authenticate(new UsernamePasswordCredentials(suser, spass))
            .andThen(result -> audit.audit(Marker.AUTHENTICATION, result.succeeded()))
            .recover(err -> Future.failedFuture(new HttpException(401, err)));
    }

    private boolean isValid(final String schema) {
        return this.type.is(schema) || Type.BEARER.is(schema);
    }
}
