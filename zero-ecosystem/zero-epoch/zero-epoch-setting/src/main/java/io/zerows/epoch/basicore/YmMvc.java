package io.zerows.epoch.basicore;

import io.zerows.epoch.basicore.option.CorsOptions;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-09
 */
@Data
public class YmMvc implements Serializable {
    private boolean freedom = false;
    private CorsOptions cors = new CorsOptions();
}
