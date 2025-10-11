package io.zerows.epoch.basicore;

import lombok.Data;

import java.io.Serializable;

/**
 * {@link YmSpec.vertx.cloud}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmCloud implements Serializable {

    private YmNacos nacos;
}
