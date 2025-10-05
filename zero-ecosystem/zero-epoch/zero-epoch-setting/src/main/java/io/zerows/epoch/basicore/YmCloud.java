package io.zerows.epoch.basicore;

import io.zerows.epoch.application.VertxYml;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link VertxYml.vertx.cloud}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmCloud implements Serializable {

    private YmNacos nacos;
}
