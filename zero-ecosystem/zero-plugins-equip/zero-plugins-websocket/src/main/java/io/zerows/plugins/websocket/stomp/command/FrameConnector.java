package io.zerows.plugins.websocket.stomp.command;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.common.Kv;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.ServerFrame;
import io.vertx.ext.stomp.StompServerConnection;
import io.vertx.ext.stomp.StompServerHandler;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.security.SecuritySession;
import io.zerows.plugins.websocket.stomp.socket.ServerWsHandler;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * This handler must be mount to Default because of the STOMP must require
 * zero-rbac connect instead of
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class FrameConnector extends AbstractFrameHandler {
    private static final String FIELD_AUTHORIZATION = HttpHeaders.AUTHORIZATION.toString().toLowerCase();

    FrameConnector(final Vertx vertx) {
        super(vertx);
    }

    @Override
    public void handle(final ServerFrame sf) {
        final String version = this.dataVersion(sf);
        final StompServerConnection connection = sf.connection();
        if (Objects.isNull(version)) {
            connection.write(FrameOutput.errorVersion(connection));
            connection.close();
            return;
        }
        this.authenticate(sf.frame(), sf.connection(), ar ->
                connection.write(FrameOutput.successConnected(connection, version)));
    }

    private void authenticate(final Frame frame, final StompServerConnection connection,
                              final Handler<AsyncResult<Void>> remainingActions) {
        if (connection.server().options().isSecured()) {
            final StompServerHandler handler = connection.handler();
            if (handler instanceof final ServerWsHandler serverHandler) {
                String authorization = frame.getHeader(FIELD_AUTHORIZATION);
                if (Ut.isNil(authorization)) {
                    authorization = frame.getHeader(HttpHeaders.AUTHORIZATION.toString());
                }
                if (Ut.isNil(authorization)) {
                    connection.write(FrameOutput.errorAuthenticate(connection));
                    connection.close();
                    return;
                }
                final JsonObject token = this.authenticateToken(authorization);
                token.put(KName.SESSION, connection.session());
                this.authenticateRetry(serverHandler, connection, token, authorization, remainingActions, false);
                return;
            }
            this.authenticateOriginal(frame, connection, remainingActions);
            return;
        }
        remainingActions.handle(Future.succeededFuture());
    }

    private void authenticateRetry(final ServerWsHandler serverHandler,
                                   final StompServerConnection connection,
                                   final JsonObject token,
                                   final String authorization,
                                   final Handler<AsyncResult<Void>> remainingActions,
                                   final boolean retried) {
        serverHandler.onAuthenticationRequest(connection, token, ar -> {
            if (ar.succeeded() && Boolean.TRUE.equals(ar.result())) {
                remainingActions.handle(Future.succeededFuture());
                return;
            }
            if (retried) {
                log.warn("[ PLUG ] ( Stomp ) auth provider rejected connect: session = {}",
                    connection.session());
                connection.write(FrameOutput.errorAuthenticate(connection));
                connection.close();
                return;
            }
            this.restoreAuthentication(authorization).onComplete(restored -> {
                if (restored.succeeded()) {
                    this.authenticateRetry(serverHandler, connection, token, authorization, remainingActions, true);
                } else {
                    log.warn("[ PLUG ] ( Stomp ) auth provider rejected connect: session = {}, cause = {}",
                        connection.session(), restored.cause().getMessage());
                    connection.write(FrameOutput.errorAuthenticate(connection));
                    connection.close();
                }
            });
        });
    }

    private Future<Void> restoreAuthentication(final String authorization) {
        final TokenType type = TokenType.fromString(authorization);
        if (Objects.isNull(type) || !TokenBuilderManager.of().isSupport(type)) {
            return Future.failedFuture("Unsupported token type.");
        }
        final String[] authorizationData = authorization.split(" ", 2);
        if (authorizationData.length < 2 || Ut.isNil(authorizationData[1])) {
            return Future.failedFuture("Missing token content.");
        }
        final String token = authorizationData[1].trim();
        final Future<Kv<String, TokenType>> subjectFuture =
            TokenBuilderManager.of().getOrCreate(type).tokenOf(token).compose();
        return subjectFuture.compose(subject -> {
                if (Objects.isNull(subject) || Ut.isNil(subject.key())) {
                    return Future.failedFuture("Unable to parse token subject.");
                }
                final Future<UserAt> userFuture = UserSession.of().find(subject.key()).compose();
                return userFuture;
            })
            .compose(userAt -> {
                if (Objects.isNull(userAt)) {
                    return Future.failedFuture("Unable to resolve logged user.");
                }
                return SecuritySession.of().authorized401(userAt, token).map((Void) null);
            });
    }

    private JsonObject authenticateToken(final String authorization) {
        final String[] authorizationData = authorization.split(" ", 2);
        if (authorizationData.length < 2 || Ut.isNil(authorizationData[1])) {
            return new JsonObject().put(FIELD_AUTHORIZATION, authorization);
        }
        final String tokenString = authorizationData[1].trim();
        final JsonObject request = new JsonObject();
        request.put(FIELD_AUTHORIZATION, authorization);
        request.put(KName.ACCESS_TOKEN, tokenString);
        request.put(KName.TOKEN, tokenString);
        return request;
    }

    private void authenticateOriginal(final Frame frame, final StompServerConnection connection,
                                      final Handler<AsyncResult<Void>> remainingActions) {
        final String login = frame.getHeader(Frame.LOGIN);
        final String passcode = frame.getHeader(Frame.PASSCODE);
        connection.handler().onAuthenticationRequest(connection, login, passcode).onComplete(ar -> {
            if (ar.succeeded() && Boolean.TRUE.equals(ar.result())) {
                remainingActions.handle(Future.succeededFuture());
            } else {
                connection.write(FrameOutput.errorAuthenticate(connection));
                connection.close();
            }
        });
    }
}
