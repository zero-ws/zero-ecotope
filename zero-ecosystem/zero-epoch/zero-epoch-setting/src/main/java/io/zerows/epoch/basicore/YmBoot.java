package io.zerows.epoch.basicore;

import io.zerows.epoch.application.VertxYml;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link VertxYml.boot}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmBoot implements Serializable {
    private UnitComponent pre;
    private UnitComponent on;
    private UnitComponent off;
}
