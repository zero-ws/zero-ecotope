package io.zerows.cosmic.plugins.websocket;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.spec.options.SockOptions;
import io.zerows.specification.development.compiled.HBundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-06-26
 */
public class AxisPublish implements Axis {
    /**
     * 此配置仅用于 SockJs，它表示当前通道直接开启非安全通道，公共 WebSocket 端口
     */
    private final Set<Remind> sockOk = new HashSet<>();

    public AxisPublish() {
        this.sockOk.addAll(SockGrid.wsPublish());
    }

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        final Router router = server.refRouter();
        final Vertx vertx = server.refVertx();
        Objects.requireNonNull(router);

        final SockOptions options = server.configSock();
        final JsonObject sockJsOptionJ = options.configSockJs();
        this.sockOk.forEach(sock -> {
            /* 此处已经做过过滤，所以 Remind 对象中的 secure = false，框架底层使用如下方式定义这种类型的 WebSocket */
            final Route route = router.route();
            final String path = KWeb.ADDR.API_WEBSOCKET + "/*";
            route.path(path).order(KWeb.ORDER.SOCK);


            /* config -> SockJsHandlerOptions */
            final SockJSHandlerOptions sockJsOptions = new SockJSHandlerOptions(sockJsOptionJ);
            final SockJSHandler handler = SockJSHandler.create(vertx, sockJsOptions);
            route.subRouter(handler.socketHandler(socket -> {
                // 执行特殊代码逻辑
            }));
        });
    }
}
