package io.zerows.epoch.configuration.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockOptions implements Serializable {

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config = new JsonObject();
    private String publish;

    private String component;

    @JsonIgnore
    private HttpServerOptions serverOptions;

    /*
     * The `input` parameter data structure should be:
     *
     * The yml formatFail
     *
     * config:
     *    port:
     * websocket:
     *    publish:
     *    component:
     *    config:
     *       stomp:
     *       bridge:
     *       handler:
     *    server:
     *
     * 1) If the `config -> port` is the same as `HTTP` server, it will be mount to
     *    HTTP server here Or the Vert.x instance will ignore other kind of server.
     * 2) The configuration is as following:
     *    - publish:    Whether the framework enable the publish websocket
     *                  The publish websocket path is `/ws/`
     *    - component:  The component should be `Ares` component here, different kind of
     *                  implementation require different component, the default is `bridge sockJs`.
     *    - config:     The configuration for different implementation
     *        - stomp:  「mode = STOMP」stomp handler configuration
     *        - bridge: 「mode = SockJs Bridge」bridge sockJs handler configuration
     *        - handler:「mode = SockJs」direct configure the sockJs handler
     *    - server:     Here are two choice of server type:
     *        - 「StompServer」 Stomp Server Configure
     *        - 「SockServer」  WebSocket Server Configure
     *
     * The input parameter is `websocket` node data of JsonObject
     */
    public JsonObject getConfig() {
        return this.config;
    }

    public void setConfig(final JsonObject config) {
        this.config = config;
    }

    public String getPublish() {
        return this.publish;
    }

    public void setPublish(final String publish) {
        this.publish = publish;
    }

    public String getComponent() {
        return this.component;
    }

    public void setComponent(final String component) {
        this.component = component;
    }

    /*
     * Three configuration key for different usage
     * 1. configSockJs
     * 2. configBridge
     * 3. configStomp
     * But current method is only for `publish` websocket channel
     */
    public JsonObject configSockJs() {
        return Ut.valueJObject(this.config, KName.HANDLER);
    }

    public SockOptions options(final HttpServerOptions serverOptions) {
        if (Objects.isNull(this.serverOptions)) {
            // 直接设置
            this.serverOptions = serverOptions;
        } else {
            // 不同时桥接
            if (this.serverOptions != serverOptions) {
                /*
                 * 拷贝 this.serverOptions 中的 websocket 配置到 serverOptions 中，并且将 this.serverOptions 引用更改成变化之
                 * 后的 serverOptions，以完成 WebSocket 选项的配置，这种方式为桥接方式，非直接设置模式，此逻辑的主要目的是为了合并
                 * WebSocket 的相关配置到当前配置的引用中形成统一配置。
                 */
                serverOptions.setWebSocketAllowServerNoContext(this.serverOptions.getWebSocketAllowServerNoContext());
                serverOptions.setWebSocketClosingTimeout(this.serverOptions.getWebSocketClosingTimeout());
                serverOptions.setWebSocketCompressionLevel(this.serverOptions.getWebSocketCompressionLevel());
                serverOptions.setWebSocketPreferredClientNoContext(this.serverOptions.getWebSocketPreferredClientNoContext());
                /* Here must include stomp sub protocols */
                serverOptions.setWebSocketSubProtocols(this.serverOptions.getWebSocketSubProtocols());

                serverOptions.setMaxWebSocketFrameSize(this.serverOptions.getMaxWebSocketFrameSize());
                serverOptions.setMaxWebSocketMessageSize(this.serverOptions.getMaxWebSocketMessageSize());
                serverOptions.setPerFrameWebSocketCompressionSupported(this.serverOptions.getPerFrameWebSocketCompressionSupported());
                serverOptions.setPerMessageWebSocketCompressionSupported(this.serverOptions.getPerMessageWebSocketCompressionSupported());

                this.serverOptions = serverOptions;
            }
        }
        return this;
    }

    public HttpServerOptions options() {
        return this.serverOptions;
    }
}
