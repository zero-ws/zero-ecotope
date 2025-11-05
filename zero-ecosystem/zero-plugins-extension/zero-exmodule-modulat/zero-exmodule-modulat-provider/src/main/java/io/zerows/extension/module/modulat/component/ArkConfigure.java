package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.module.modulat.common.Bk.LOG;

class ArkConfigure extends ArkBase {

    /*
     * 该方法会提取应用存储专用的配置信息，该配置信息会被存储到 B_BLOCK 表中，开发中心可修订
     * 配置程序，如：
     * {
     *     "value": "mXXX"
     * }
     * 格式，最终使用时用
     * mXXX = configuration
     * 的 JSON 格式
     *
     * 输入的格式
     */
    // private static final Cc<String, Future<JsonObject>> ASYNC_BAG_ADMIN = Cc.openA();

    @Override
    public Future<ClusterSerializable> modularize(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        if (open) {
            // open = true，跳过缓存
            return this.modularizeInternal(appId, true, by).compose(Ux::future);
        }
        // open = false，启用缓存
        return null;

        //        return ASYNC_BAG_ADMIN.pick(
        //                () -> this.modularizeInternal(id, false, by), id)
        //            .compose(Ux::future);
    }

    private Future<JsonObject> modularizeInternal(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        final JsonObject condition = this.buildQr(appId, by);
        /*
         * 新路由中，BAG直接提取 EXTENSION 类型的模型即可
         * parentId __ NULL 在旧版本中是可行的，旧版本没有入口根包的概念
         * 新版本中多了入口根包概念，所以就不可以使用这个条件了，否则会导致BLOCK
         * 为空。
         */
        condition.put(KName.PARENT_ID + ",n", null);
        LOG.Spi.info(this.getClass(), "Modulat condition = {0}", condition.encode());
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition)
            .compose(bags -> this.configureOut(bags, open));
    }

    private Future<JsonObject> configureOut(final List<BBag> bags, final boolean open) {
        final ConcurrentMap<String, Future<JsonObject>> futures = new ConcurrentHashMap<>();
        // open = true 的时候要开放
        final ConcurrentMap<String, Set<String>> openMap = new ConcurrentHashMap<>();
        bags.forEach(bag -> {
            final JsonObject uiConfig = Ut.toJObject(bag.getUiConfig());
            final String configKey = Ut.valueString(uiConfig, KName.STORE);
            final JsonArray openField = Ut.valueJArray(uiConfig, "open");
            if (Ut.isNotNil(openField)) {
                openMap.put(configKey, Ut.toSet(openField));
            }
            if (Ut.isNotNil(configKey)) {
                futures.put(configKey, this.configureBag(bag));
            }
        });
        return Fx.combineM(futures).compose(dataMap -> {
            if (open) {
                final JsonObject rapidJ = Ut.toJObject(dataMap);
                return Ux.future(rapidJ);
            }
            // 带过滤的模式
            final JsonObject result = new JsonObject();
            dataMap.forEach((configKey, data) -> {
                // 如果开放则必须使用开放配置来做参数
                if (openMap.containsKey(configKey)) {
                    // 过滤
                    final Set<String> fieldSet = openMap.get(configKey);
                    final JsonObject filtered = Ut.elementSubset(data, fieldSet);
                    result.put(configKey, filtered);
                }
            });
            return Ux.future(result);
        });
    }
}
