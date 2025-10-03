package io.zerows.component.destine;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.specification.KJoin;

/**
 * @author lang : 2023-07-31
 */
class ConflateJQr extends ConflateBase<JsonObject, JsonObject> {

    ConflateJQr(final KJoin joinRef) {
        super(joinRef);
    }

    @Override
    public JsonObject treat(final JsonObject active, final String identifier) {
        // 提取连接点数据
        return this.procQr(active, identifier);
    }
}
