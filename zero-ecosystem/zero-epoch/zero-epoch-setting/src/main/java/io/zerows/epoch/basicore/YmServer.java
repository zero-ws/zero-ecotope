package io.zerows.epoch.basicore;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.VertxYml;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link VertxYml.server}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmServer implements Serializable {
    private int port;
    private String address;
    private JsonObject options = new JsonObject();
    private YmWebSocket websocket = new YmWebSocket();
}
