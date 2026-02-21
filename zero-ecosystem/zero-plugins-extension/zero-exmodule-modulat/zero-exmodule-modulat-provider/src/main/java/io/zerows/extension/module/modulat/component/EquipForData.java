package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.modeling.EmModel;

public class EquipForData extends EquipForBase {
    private static final Cc<String, JsonObject> FULL_DATA = Cc.open();

    @Override
    public Future<JsonObject> configure(final String appId, final EmModel.By by) {
        if (FULL_DATA.containsKey(appId)) {
            return Future.succeededFuture(FULL_DATA.get(appId));
        }
        final JsonObject condition = this.buildQr(appId, by);
        return this.fetchBags(condition)
            .compose(map -> this.dataAsync(map, false))
            .map(result -> {
                FULL_DATA.put(appId, result);
                return result;
            });
    }
}
