package io.zerows.epoch.basicore.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class SockOptions implements Serializable {

    public SockOptions() {
    }

    public SockOptions(final JsonObject options, final Class<?> executor) {
        this.publish = Ut.valueString(options, "publish");
        this.config.mergeIn(options, true);
        this.component = executor;
    }

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config = new JsonObject();
    private String publish;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> component;

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

    public HttpServerOptions serverOptions() {
        return this.serverOptions;
    }
}
