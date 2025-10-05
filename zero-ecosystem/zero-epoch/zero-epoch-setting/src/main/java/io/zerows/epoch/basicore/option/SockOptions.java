package io.zerows.epoch.basicore.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class SockOptions implements Serializable {

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config = new JsonObject();
    private String publish;

    private String component;

    @JsonIgnore
    private HttpServerOptions serverOptions;

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
