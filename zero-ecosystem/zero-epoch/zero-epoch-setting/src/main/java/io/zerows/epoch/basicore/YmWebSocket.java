package io.zerows.epoch.basicore;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.Equip;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link YmSpec.server.websocket}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmWebSocket implements Serializable, Equip.Item {
    private String publish;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> component;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config = new JsonObject();

    @Override
    public JsonObject combined() {
        final JsonObject options = this.config.copy();
        if (StrUtil.isNotBlank(this.publish)) {
            options.put("publish", this.publish);
        }
        return options;
    }

    /**
     * {@link YmSpec.server.websocket.config.stomp}
     */
    @Data
    public static class Stomp implements Serializable {
        private int port;
        private boolean secured;
        private boolean websocketBridge;
        private String websocketPath;
        private String stomp;
        private String bridge;

        @JsonSerialize(using = ClassSerializer.class)
        @JsonDeserialize(using = ClassDeserializer.class)
        private Class<?> handler;
    }
}
