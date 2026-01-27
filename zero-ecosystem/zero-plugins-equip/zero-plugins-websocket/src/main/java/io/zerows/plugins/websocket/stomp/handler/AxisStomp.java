package io.zerows.plugins.websocket.stomp.handler;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.epoch.spec.options.SockOptions;
import io.zerows.plugins.websocket.stomp.socket.ServerWsHandler;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-06-26
 */
@Slf4j
public class AxisStomp implements Axis {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);

    @Override
    public void mount(final RunServer server, final HBundle bundle) {

        // 挂载 AxisStomp 相关内容
        final SockOptions sockOptions = server.configSock();
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
            final SecurityMeta aegis = mAuthorize.mount(handler, stompOptions);

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

        if (IS_OUT.getAndSet(Boolean.FALSE)) {
            final String endpoint = this.pathOfEndpoint(sockOptions);
            log.info("[ PLUG ] ( WebSocket ) \uD83E\uDDFF Endpoint: ws://{}:{}{}，{} ✅️ WebSocket 成功开启 SUCCESS !",
                Ut.netIPv4(), httpServer.actualPort(), endpoint, this.getClass().getSimpleName());
        }
    }

    private String pathOfEndpoint(final SockOptions sockOptions) {
        final JsonObject config = sockOptions.getConfig();
        final JsonObject stomp = Ut.valueJObject(config, "stomp");
        return stomp.getString("websocketPath");
    }
}
