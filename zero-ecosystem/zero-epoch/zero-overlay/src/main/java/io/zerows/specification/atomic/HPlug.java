package io.zerows.specification.atomic;

import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2025-10-13
 */
public interface HPlug {

    default <T extends HPlug> HPlug bind(final JsonObject configJ) {
        return this;
    }
}
