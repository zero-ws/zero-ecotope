package io.zerows.epoch.spec;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.spec.options.CorsOptions;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-09
 */
@Data
public class YmMvc implements Serializable, YmPriority {
    private boolean freedom = false;
    private CorsOptions cors = new CorsOptions();
    private ConcurrentMap<String, String> resolver;

    @Override
    public JsonObject combined() {
        final JsonObject mvcJ = new JsonObject();
        mvcJ.put("freedom", this.freedom);
        mvcJ.put("resolver", this.resolver);
        return mvcJ;
    }
}
