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
import io.zerows.plugins.monitor.client.QuotaMetric;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
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
    private static final Cc<String, JsonObject> BAG_ADMIN = Cc.open();

    @Override
    public Future<ClusterSerializable> modularize(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        if (open) {
            // 跳过缓存
            return this.modularizeInternal(appId, true, by)
                .compose(Ux::future);
        }
        // 启用缓存
        if (BAG_ADMIN.containsKey(appId)) {
            return Ux.future(BAG_ADMIN.get(appId));
        }
        return this.modularizeInternal(appId, false, by).compose(stored -> {
            BAG_ADMIN.put(appId, stored);
            return Ux.future(stored);
        });
    }
    // private static final Cc<String, Future<JsonObject>> ASYNC_BAG_ADMIN = Cc.openA();

    private Future<JsonObject> modularizeInternal(final String appId,
                                                  final boolean open,
                                                  final EmModel.By by) {
        /*
         * 📘[JSON] --> 查询条件
         * {
         *     "": true,
         *     "appId": "???",
         *     "type,i": ["EXTENSION", "COMMERCE", "FOUNDATION"],
         *     "parentId,n": null
         * }
         * 新版路由中，BAG 会直接提取 EXTENSION 类型的模型信息，parentId = NULL 在旧版中可用，因为旧版没有入口根包的概念，而新版中会包含一个入口
         * 根包，检查 B_BAG 数据表
         */
        final JsonObject condition = this.buildQr(appId, by);
        condition.put(KName.PARENT_ID + ",n", null);
        log.debug("{} 模块集合条件：{}", BkConstant.K_PREFIX, condition.encode());
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition).compose(bags -> {
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
        });
    }

    static class Mom implements QuotaMetric.Supervisor<String, JsonObject> {

        @Override
        public Set<String> keys() {
            return BAG_ADMIN.keySet();
        }

        @Override
        public JsonObject value(final String key) {
            return BAG_ADMIN.getOrDefault(key, null);
        }
    }
}
