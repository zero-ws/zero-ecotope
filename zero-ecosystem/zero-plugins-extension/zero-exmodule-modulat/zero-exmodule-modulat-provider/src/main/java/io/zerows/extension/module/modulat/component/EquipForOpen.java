package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.modeling.EmModel;

public class EquipForOpen extends EquipForBase {
    private static final Cc<String, JsonObject> OPEN_DATA = Cc.open();

    @Override
    public Future<JsonObject> configure(final String appId, final EmModel.By by) {
        if (OPEN_DATA.containsKey(appId)) {
            return Future.succeededFuture(OPEN_DATA.get(appId));
        }
        final JsonObject condition = this.buildQr(appId, by);
        return this.fetchBags(condition)
            .compose(map -> this.dataAsync(map, true))
            .map(result -> {
                result.put(KName.KEY,appId);
                OPEN_DATA.put(appId, result);
                return result;
            });
    }
}
