package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.program.Ux;

import java.util.List;

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
            .compose(result -> this.attachApps(result, appId, by))
            .map(result -> {
                FULL_DATA.put(appId, result);
                return result;
            });
    }

    // 将当前 appId 对应的 B_BAG 原始列表作为 apps 节点挂载到结果中
    private Future<JsonObject> attachApps(final JsonObject result, final String appId, final EmModel.By by) {
        final JsonObject condition = this.buildQr(appId, by);
        condition.put(KName.ENTRY+",!n", "");
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition)
            .map((List<BBag> bags) -> {
                JsonArray json = Ux.toJson(bags);

                result.put(KName.App.APPS, json);
                return result;
            });
    }
}
