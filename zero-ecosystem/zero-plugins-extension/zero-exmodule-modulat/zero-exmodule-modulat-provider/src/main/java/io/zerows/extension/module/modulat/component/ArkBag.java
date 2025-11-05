package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.platform.enums.modeling.EmModel;

class ArkBag extends ArkBase {
    /*
     * B_BAG 专用数据存储结构，以及 B_BAG 核心缓存结构，用于加载 X_BAG 中的基础数据
     * ENTRY = true AND ENTRY_ID __ NOT NULL
     *
     * 这种场景下会为所有的 modules 构造 ( Bag = App ) 的基础入口配置，而页面模型则会直接在
     */
    // private static final Cc<String, JsonArray> ASYNC_BAG_DATA = Cc.open();

    @Override
    public Future<ClusterSerializable> modularize(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        // R2MO 有问题
        //        return ASYNC_BAG_DATA.pick(() -> {
        //            final JsonObject condition = this.buildQr(id, by);
        //            condition.put(KName.ENTRY, Boolean.TRUE);
        //            LOG.Spi.info(this.getClass(), "Modulat condition = {0}", condition.encode());
        //            return Ux.Jooq.join(BBagDao.class).<BBag>fetchAsync(condition)
        //                // JsonArray -> ClusterSerializable
        //                .compose(Ux::futureA);
        //        }, id).compose(Ux::future);
        return null;
    }
}
