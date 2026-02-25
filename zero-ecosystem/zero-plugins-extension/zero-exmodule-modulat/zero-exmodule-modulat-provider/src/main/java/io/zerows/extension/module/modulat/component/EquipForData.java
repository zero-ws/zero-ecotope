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

    /**
     * 在 open = false 的场景下，XApp + BBag 是一对多的关系，但多端依赖的不是 XApp 部分，而是 BBag 的树型结构
     * <pre>
     *                                 XApp ( appId )
     *     - Bag-01 /                     app-01
     *           - Bag-0101               .....
     *           - Bag-0102               .....
     *     - Bag-02 /                     .....
     *           - Bag-0201               .....
     *           - Bag-0202               .....
     * </pre>
     * 最终的 App 结构
     * <pre>
     *     - mXxx
     *     - mYyy
     *     - apps ( 替换旧版 bags )
     * </pre>
     *
     * @param result 当前构造的结果集
     * @param appId  应用ID
     * @param by     查询方式
     * @return 附加了应用信息的结果集
     */
    private Future<JsonObject> attachApps(final JsonObject result, final String appId, final EmModel.By by) {
        final JsonObject condition = this.buildQr(appId, by);
        condition.put(KName.ENTRY + ",!n", "");
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition).map((final List<BBag> bags) -> {
            final JsonArray json = Ux.toJson(bags);
            result.put(KName.APPS, json);
            result.put(KName.ID, appId);
            return result;
        });
    }
}
