package io.zerows.extension.module.integration.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.integration.domain.tables.daos.IDirectoryDao;
import io.zerows.extension.module.integration.domain.tables.pojos.IDirectory;
import io.zerows.extension.module.integration.util.Is;
import io.zerows.support.Ut;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class FsBase implements Fs {
    /**
     * 此方法为初始化文档存储的专用方法，且实现基于子类来完成
     *
     * @param data   传入的目录数据
     * @param config 传入的配置数据
     * @return 返回初始化后的数据
     */
    protected Future<JsonArray> initialize(final JsonArray data, final JsonObject config) {
        /*
         * 步骤一：根据传入数据执行初始化行为，传入数据中
         * - 如果带有 key 则证明是已经初始化过的数据，直接返回
         * - 如果不带 key 则证明是新数据，执行初始化（添加逻辑）
         */
        final JsonArray formatted = data.copy();
        Ut.itJArray(formatted).forEach(json -> {
            if (!json.containsKey(KName.KEY)) {
                json.put(KName.KEY, UUID.randomUUID().toString());
            }
        });


        /*
         * 步骤二：分组执行
         * - storePath,   根据存储路径的分组
         * - storeParent, 根据存储路径中的父路径的分组
         */
        final ConcurrentMap<String, JsonObject> stored = Ut.elementMap(formatted, KName.STORE_PATH);
        final ConcurrentMap<String, JsonArray> storeParent = Ut.elementGroup(formatted, KName.STORE_PARENT);


        /*
         * 步骤三：执行所有父路径的严格模式读取，若前边传入已经存在路径信息，则这种读取方法会直接读取这些传入路径的所有父路径信息
         * - 1. 直接使用 IN 语法严格读取所有付路径 storeParent 关联的存储目录数据
         * - 2. 读取的数据会被存储到 storeMap 中，storePath = IDirectory（父路径目录中的键为 storePath 属性对应的值）
         */
        return Is.directoryQr(formatted, KName.STORE_PARENT, true).compose(queried -> {
            final ConcurrentMap<String, IDirectory> storeMap = Ut.elementMap(queried, IDirectory::getStorePath);


            // 此处直接提取配置数据中的 initialize 属性的相关信息，此属性中包含了初始化的基础信息
            final JsonObject initialize = config.getJsonObject(KName.INITIALIZE, new JsonObject());


            /*
             * 步骤四：添加队列计算，此处方法只执行初始化，所以只做数据插入，若本身存在，由于是初始化生命周期，依旧不做任何
             * 数据同步的操作（不执行UPDATE），而在添加过程中根据 initialize 中的基础配置数据执行两种初始化
             * - 基础初始化（可重写），调用内置方法 initialize
             * - 父目录结构初始化（可重写），调用内置方法 initialize
             */
            final List<IDirectory> inserted = new ArrayList<>();
            storeParent.forEach((pathParent, dataGroup) -> {
                final IDirectory storeObj = storeMap.get(pathParent);
                final JsonObject storeInput = stored.get(pathParent);
                Ut.itJArray(dataGroup).forEach(json -> {
                    final JsonObject dataRecord = json.copy();
                    final JsonObject directoryJ = this.initialize(dataRecord, initialize);
                    final IDirectory normalized = this.initialize(directoryJ, storeObj, storeInput);
                    inserted.add(normalized);
                });
            });
            return DB.on(IDirectoryDao.class).insertJAsync(inserted);
        });
    }

    protected JsonObject initialize(final JsonObject data, final JsonObject initialize) {
        final JsonObject directoryJ = new JsonObject();
        Ut.valueCopy(directoryJ, data,
            // key for inserted
            KName.KEY,
            // category, name, storePath
            KName.NAME,
            KName.CATEGORY,
            KName.STORE_PATH,
            // active, language, sigma
            KName.ACTIVE,
            KName.LANGUAGE,
            KName.SIGMA,
            // runComponent
            KName.Component.RUN_COMPONENT
        );
        final String USER_SYSTEM = "zero-environment";
        // updatedAt, updatedBy, createdAt, createdBy, owner
        directoryJ.put(KName.UPDATED_AT, Instant.now());
        directoryJ.put(KName.CREATED_AT, Instant.now());
        directoryJ.put(KName.UPDATED_BY, USER_SYSTEM);
        directoryJ.put(KName.CREATED_BY, USER_SYSTEM);
        directoryJ.put(KName.OWNER, USER_SYSTEM);
        // Visit
        directoryJ.put(KName.VISIT, Boolean.FALSE);
        final JsonArray visitMode;
        if (initialize.containsKey(KName.VISIT_MODE)) {
            visitMode = initialize.getJsonArray(KName.VISIT_MODE);
        } else {
            visitMode = new JsonArray().add("r").add("w").add("x");
        }
        directoryJ.put(KName.VISIT_MODE, visitMode.encode());
        return directoryJ;
    }

    protected IDirectory initialize(final JsonObject directoryJ, final IDirectory parentD, final JsonObject parentJ) {
        // Calculated by Parent
        if (Objects.nonNull(parentD)) {
            directoryJ.put(KName.TYPE, parentD.getType());
            directoryJ.put(KName.OWNER, parentD.getOwner());
            directoryJ.put(KName.INTEGRATION_ID, parentD.getIntegrationId());
            directoryJ.put(KName.Component.RUN_COMPONENT, parentD.getRunComponent());
            directoryJ.put(KName.PARENT_ID, parentD.getKey());

            directoryJ.put(KName.VISIT_ROLE, parentD.getVisitRole());
            directoryJ.put(KName.VISIT_GROUP, parentD.getVisitGroup());
            directoryJ.put(KName.VISIT_COMPONENT, parentD.getVisitComponent());
        } else if (Ut.isNotNil(parentJ)) {
            directoryJ.put(KName.PARENT_ID, parentJ.getValue(KName.KEY));
            if (!directoryJ.containsKey(KName.Component.RUN_COMPONENT)) {
                directoryJ.put(KName.Component.RUN_COMPONENT, parentJ.getString(KName.Component.RUN_COMPONENT));
            }
        }
        return this.initTree(directoryJ);
    }
}
