package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.common.BkConstant;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ArkBag extends ArkBase {
    /*
     * B_BAG 专用数据存储结构，以及 B_BAG 核心缓存结构，用于加载 X_BAG 中的基础数据
     * ENTRY = true AND ENTRY_ID __ NOT NULL
     *
     * 这种场景下会为所有的 modules 构造 ( Bag = App ) 的基础入口配置，而页面模型则会直接在
     */
    private static final Cc<String, Future<JsonArray>> ASYNC_BAG_DATA = Cc.openThread();

    @Override
    public Future<ClusterSerializable> modularize(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        return ASYNC_BAG_DATA.pick(() -> {
            final JsonObject condition = this.buildQr(appId, by);
            condition.put(KName.ENTRY, Boolean.TRUE);
            log.debug("{} 模块配置条件 = `{}`", BkConstant.K_PREFIX, condition.encode());
            return DB.on(BBagDao.class).<BBag>fetchAsync(condition)
                .compose(Ux::futureA);
        }, appId).compose(Ux::future);

    }
}
