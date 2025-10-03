package io.zerows.plugins.websocket.stomp.handler;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.epoch.configuration.option.SockOptions;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.support.Ut;
import io.zerows.plugins.websocket.stomp.socket.ServerWsHandler;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
public class AxisStomp implements OAxis {
    @Override
    public void mount(final RunServer server, final Bundle bundle) {
        // 配置扩展
        this.mountOption(server);

        // 挂载 AxisStomp 相关内容
        final SockOptions sockOptions = server.configSock().options();
        Objects.requireNonNull(sockOptions);
        final JsonObject stompJ = Ut.valueJObject(sockOptions.getConfig(), "stomp");
        final StompServerOptions stompOptions = new StompServerOptions(stompJ);


        final Vertx vertx = server.refVertx();
        final StompServer stompServer = StompServer.create(vertx, stompOptions);
        // Iterator the SOCKS
        final ServerWsHandler handler = ServerWsHandler.create(vertx);

        {
            // Security for WebSocket
            final Mixer mAuthorize =
                Mixer.instance(MixerAuthorize.class, vertx);
            final Aegis aegis = mAuthorize.mount(handler, stompOptions);

            // Mount user definition handler
            final Mixer mHandler =
                Mixer.instance(MixerHandler.class, vertx, aegis);
            mHandler.mount(handler);

            // Mount event bus
            final Mixer mBridge =
                Mixer.instance(MixerBridge.class, vertx);
            mBridge.mount(handler, stompOptions);

            // Mount destination
            final Mixer mDestination =
                Mixer.instance(MixerDestination.class, vertx);
            mDestination.mount(handler);
        }

        // Build StompServer and bind webSocketHandler
        stompServer.handler(handler);
        final HttpServer httpServer = server.instance();
        httpServer.webSocketHandler(stompServer.webSocketHandler());
    }

    private void mountOption(final RunServer server) {
        // 当前环境的 HttpServerOptions
        final HttpServerOptions serverOptions = server.config().options();
        Objects.requireNonNull(serverOptions);
        final SockOptions sockOptions = server.configSock().options();
        Objects.requireNonNull(sockOptions);

        // 是否执行配置扩展
        final HttpServerOptions configured = sockOptions.options();
        if (Objects.isNull(configured)) {
            return;
        }

        if (serverOptions == configured) {
            // 已经桥接
            return;
        }
        sockOptions.options(serverOptions);
    }
}
