package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.modeling.EmModel;

public class EquipForData extends EquipForBase {
    @Override
    public Future<JsonObject> configure(final String appId, final EmModel.By by) {
        final JsonObject condition = this.buildQr(appId, by);
        return this.fetchBags(condition).compose(map -> {
            return null;
        });
    }
}
