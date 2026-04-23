package io.zerows.plugins.websocket.stomp.command;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.ServerFrame;
import io.vertx.ext.stomp.StompServerConnection;
import io.vertx.ext.stomp.StompServerHandler;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.websocket.stomp.socket.ServerWsHandler;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.Objects;

/**
 * This handler must be mount to Default because of the STOMP must require
 * zero-rbac connect instead of
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class FrameConnector extends AbstractFrameHandler {
    private static final String FIELD_AUTHORIZATION = HttpHeaders.AUTHORIZATION.toLowerCase();

    FrameConnector(final Vertx vertx) {
        super(vertx);
    }

    @Override
    public void handle(final ServerFrame sf) {
        // Server Negotiation
        final String version = this.dataVersion(sf);
        final StompServerConnection connection = sf.connection();
        if (Objects.isNull(version)) {
            // Spec says: if the server and the client do not share any common protocol versions, then the server MUST
            // respond with an error.
            connection.write(FrameOutput.errorVersion(connection));
            connection.close();
            return;
        }


        /*
         * Critical code logical to replace the Login / Passcode in default handler
         * That's why zero define the own handler here.
         */
        this.authenticate(sf.frame(), sf.connection(), ar -> {
            // Spec says: The server will respond back with the highest version of the protocol -> version
            connection.write(FrameOutput.successConnected(connection, version));
        });
    }

    private void authenticate(final Frame frame, final StompServerConnection connection,
                              final Handler<AsyncResult<Void>> remainingActions) {
        if (connection.server().options().isSecured()) {
            /*
             * The Modification based join new interface to parsing the `Authorization` header
             * instead of the default web socket STOMP feature, here provider
             * {
             *     "username": "xxxx",
             *     "password": "xxxx"
             * }
             * Only, but it's not enough in zero-framework ( zero-rbac ) module, instead here should be
             * new code logical to processing the connection authorization
             *
             * Zero framework provider require following data structure based join:
             * {
             *     "access_token": "xxxx",
             *     "user": "xxxx",
             *     "habitus": "xxxx",
             *     "session": "xxxx"
             * }
             */
            final StompServerHandler handler = connection.handler();
            if (handler instanceof ServerWsHandler) {
                // Extension Code Flow
                String authorization = frame.getHeader(FIELD_AUTHORIZATION);
                if (Ut.isNil(authorization)) {
                    authorization = frame.getHeader(HttpHeaders.AUTHORIZATION);
                }
                if (Ut.isNil(authorization)) {
                    // 401 Error
                    connection.write(FrameOutput.errorAuthenticate(connection));
                    connection.close();
                } else {
                    // Extract authorization to token
                    final JsonObject token = this.authenticateToken(authorization);
                    ((ServerWsHandler) handler).onAuthenticationRequest(connection, token, ar -> {
                        if (ar.succeeded() && Boolean.TRUE.equals(ar.result())) {
                            remainingActions.handle(Future.succeededFuture());
                        } else {
                            connection.write(FrameOutput.errorAuthenticate(connection));
                            connection.close();
                        }
                    });
                }
            } else {
                // Original `DefaultConnectHandler`
                this.authenticateOriginal(frame, connection, remainingActions);
            }
        } else {
            // Other action happen
            remainingActions.handle(Future.succeededFuture());
        }
    }

    private JsonObject authenticateToken(final String authorization) {
        /*
         * Token 验证流程，处理 401 基础验证信息
         */
        final String[] authorizationData = authorization.split(" ", 2);
        if (authorizationData.length < 2 || Ut.isNil(authorizationData[1])) {
            return new JsonObject().put(FIELD_AUTHORIZATION, authorization);
        }
        final String tokenString = authorizationData[1];
        final JsonObject token = Account.userAuthorization(authorization);
        if (Objects.isNull(token)) {
            return new JsonObject().put(FIELD_AUTHORIZATION, authorization);
        }
        final JsonObject request = token.copy();
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
