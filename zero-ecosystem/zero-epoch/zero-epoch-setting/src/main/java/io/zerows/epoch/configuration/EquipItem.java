package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2025-10-09
 */
public interface EquipItem {
    /**
     * 融合之后的数据
     *
     * @return {@link JsonObject}
     */
    default JsonObject combined() {
        return null;
    }
}
