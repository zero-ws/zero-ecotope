package io.zerows.epoch.jigsaw;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.spec.options.SockOptions;

/**
 * @author lang : 2025-10-10
 */
class ProcessorWebsocket implements Processor<SockOptions, HttpServerOptions> {
    @Override
    public void makeup(final SockOptions sockOptions, final HttpServerOptions serverOptions) {
        // 正向关联
        sockOptions.setServerOptions(serverOptions);


        // 逆向关联 -> 设置 HttpServerOptions 的 websocket 支持
        final JsonObject optionsJ = sockOptions.getConfig();
        final HttpServerOptions configured = new HttpServerOptions(optionsJ);
        /*
         * 拷贝 configured 中的 websocket 配置到 serverOptions 中，并且将 configured 引用更改成变化之
         * 后的 serverOptions，以完成 WebSocket 选项的配置，这种方式为桥接方式，非直接设置模式，此逻辑的主要目的是为了合并
         * WebSocket 的相关配置到当前配置的引用中形成统一配置。
         */
        serverOptions.setWebSocketAllowServerNoContext(configured.getWebSocketAllowServerNoContext());
        serverOptions.setWebSocketClosingTimeout(configured.getWebSocketClosingTimeout());
        serverOptions.setWebSocketCompressionLevel(configured.getWebSocketCompressionLevel());
        serverOptions.setWebSocketPreferredClientNoContext(configured.getWebSocketPreferredClientNoContext());
        /* Here must include stomp sub protocols */
        serverOptions.setWebSocketSubProtocols(configured.getWebSocketSubProtocols());

        serverOptions.setMaxWebSocketFrameSize(configured.getMaxWebSocketFrameSize());
        serverOptions.setMaxWebSocketMessageSize(configured.getMaxWebSocketMessageSize());
        serverOptions.setPerFrameWebSocketCompressionSupported(configured.getPerFrameWebSocketCompressionSupported());
        serverOptions.setPerMessageWebSocketCompressionSupported(configured.getPerMessageWebSocketCompressionSupported());
    }
}
