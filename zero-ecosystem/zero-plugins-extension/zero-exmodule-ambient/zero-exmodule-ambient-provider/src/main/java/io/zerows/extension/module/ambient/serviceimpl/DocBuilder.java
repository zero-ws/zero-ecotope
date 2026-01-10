package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.boot.AtConfig;
import io.zerows.extension.module.ambient.boot.MDAmbientManager;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.module.ambient.servicespec.DocBStub;
import io.zerows.extension.skeleton.spi.ExArbor;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class DocBuilder implements DocBStub {
    private static final Cc<String, ExArbor> CC_ARBOR = Cc.openThread();
    private static final MDAmbientManager MANAGER = MDAmbientManager.of();

    // ------------------------- Document Management Tree -------------------------
    /*
     * Here the parameters are mapped to
     * -- id: X_APP -> KEY
     * -- type:  X_CATEGORY -> TYPE
     */
    @Override
    public Future<JsonArray> initialize(final String appId, final String type) {
        final JsonObject condition = this.qrCond(appId, type, null);
        return DB.on(XCategoryDao.class).fetchJAsync(condition)
            .map(item -> Ut.valueToJArray(item,
                KName.METADATA,
                KName.Component.TREE_CONFIG,
                KName.Component.RUN_CONFIG
            ))
            .compose(categories -> {
                final List<Future<JsonArray>> futures = new ArrayList<>();
                Ut.itJArray(categories).map(this::seekAsync).forEach(futures::add);
                return Fx.compressA(futures);
            });
    }

    @Override
    public Future<JsonArray> initialize(final String appId, final String type, final String name) {
        final JsonObject condition = this.qrCond(appId, type, name);
        return DB.on(XCategoryDao.class).fetchJOneAsync(condition)
            .map(item -> Ut.valueToJObject(item,
                KName.METADATA,
                KName.Component.TREE_CONFIG,
                KName.Component.RUN_CONFIG
            ))
            .compose(this::seekAsync);
    }

    private JsonObject qrCond(final String appId, final String type, final String name) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.APP_ID, appId);
        condition.put(KName.TYPE, type);
        condition.put(KName.ACTIVE, Boolean.TRUE);
        if (Ut.isNotNil(name)) {
            condition.put(KName.NAME, name);
        }
        return condition;
    }

    /*
     * Each category node should contain two dim operations:
     * -- treeComponent/treeConfig
     * -- runComponent/runConfig
     *
     * Terminal when comment in following situations
     * -- 1. treeComponent is null or it's not implement from Arbor interface.
     */
    private Future<JsonArray> seekAsync(final JsonObject input) {
        final String runComponent = input.getString(KName.Component.TREE_COMPONENT);
        final Class<?> arborCls = Ut.clazz(runComponent, null);


        // Terminal 1:
        if (Objects.isNull(arborCls) || !Ut.isImplement(arborCls, ExArbor.class)) {
            return Ux.futureA();
        }


        final JsonObject configuration = input.getJsonObject(KName.Component.TREE_CONFIG);
        JsonObject storeRef = configuration.getJsonObject(KName.STORE);
        if (Ut.isNil(storeRef)) {
            storeRef = new JsonObject();
        }
        /*
         * The configuration data came from `treeConfig`, combine the configuration information attached into
         * {
         *      "value": {
         *          "storePath": "",
         *          "runComponent": "Default `Fs` interface component that will be stored into I_DIRECTORY",
         *          "initialize": {
         *              "field1": "value1",
         *              "field2": "value2",
         *              "...": "..."
         *          }
         *      }
         * }
         */
        final AtConfig config = Objects.requireNonNull(MANAGER.config());
        storeRef.put(KName.STORE_PATH, config.getStorePath());
        configuration.put(KName.STORE, storeRef);

        final ExArbor arbor = CC_ARBOR.pick(() -> Ut.instance(arborCls), arborCls.getName());
        log.info("{} Arbor 组件：{}, 配置：{}", AtConstant.K_PREFIX_AMB, arborCls.getName(), configuration.encode());
        return arbor.generate(input, configuration);
    }
}
