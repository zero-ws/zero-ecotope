package io.zerows.extension.runtime.integration.util;

import io.zerows.ams.constant.em.typed.ChangeFlag;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.fn.Fx;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.integration.domain.tables.pojos.IDirectory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-07-03
 */
class IsStore {

    private static ConcurrentMap<ChangeFlag, JsonArray> diff(final JsonArray input, final List<IDirectory> directories) {
        /*
         * 将数据库中读取的目录基础数据转换成 Map，构造结构：
         * - storePath = IDirectory 的数据结构，简单说就是构造基于真实路径（相对）的哈希表，得到目录对象引用
         *
         * 之后此方法会构造三个核心队列
         * - queueAD: 添加队列，当前目录在数据库中不存在，需要直接添加
         *   - 添加数据记录
         *   - 添加目录基本记录
         * - queueUP: 更新队列，当前目录在数据库中存在，需要直接更新
         *   - 添加数据记录
         *   - 同步目录基本记录（存在则不管、不存在则构造）
         * - queueDft：遍历更新队列时，一旦发现更新队列，哈希表会被移除掉对应的 storePath 之下的目录对象，若未存在于
         *   更新队列中的 IDirectory 则表示该目录已经存在于数据库中，这种单纯存在于数据库中的目录信息则需要构造 directoryId 属性
         *   这种队列的基础数据应该执行目录同步操作
         */
        final ConcurrentMap<String, IDirectory> directoryMap = Ut.elementMap(directories, IDirectory::getStorePath);
        final JsonArray queueAD = new JsonArray();
        final JsonArray queueUP = new JsonArray();

        Ut.itJArray(input).forEach(json -> {
            final String path = json.getString(KName.STORE_PATH);
            if (directoryMap.containsKey(path)) {
                // UPDATE Queue
                final JsonObject normalized = Ux.toJson(directoryMap.getOrDefault(path, null));
                queueUP.add(normalized);
                directoryMap.remove(path);
            } else {
                // ADD Queue
                queueAD.add(json);
            }
        });
        final JsonArray queueDft = new JsonArray();
        if (!directoryMap.isEmpty()) {
            directoryMap.values().forEach(item -> {
                final JsonObject record = Ux.toJson(item);
                record.put(KName.DIRECTORY_ID, item.getKey());
                queueDft.add(record);
            });
        }
        return new ConcurrentHashMap<>() {
            {
                this.put(ChangeFlag.ADD, queueAD);
                this.put(ChangeFlag.UPDATE, queueUP);
                this.put(ChangeFlag.NONE, queueDft);
            }
        };
    }

    static Future<JsonArray> document(final JsonArray data, final JsonObject config) {
        /*
         * 非严格模式提取目录数据
         */
        return IsDir.query(data, KName.STORE_PATH, false)
            .compose(queried -> {
                final ConcurrentMap<ChangeFlag, JsonArray> compared = diff(data, queried);
                /*
                 * - ADD 队列，负责添加，添加队列要将 directoryId 追加
                 * - UPDATE 队列，更新队列，更新队列已包含了 directoryId 则无所谓
                 * - NONE 队列，已经存在于数据库中的目录，需要构造 directoryId
                 */
                final List<Future<JsonArray>> futures = new ArrayList<>();
                futures.add(mkdir(compared.getOrDefault(ChangeFlag.ADD, new JsonArray()), config));
                futures.add(mkdir(compared.getOrDefault(ChangeFlag.UPDATE, new JsonArray()), queried));
                futures.add(Ux.future(compared.getOrDefault(ChangeFlag.NONE, new JsonArray())));
                return Fx.compressA(futures);
            })
            .compose(synced -> IsFs.run(synced, (fs, dataGroup) -> fs.synchronize(dataGroup, config)))
            .compose(synced -> {
                Ut.itJArray(synced).forEach(json -> Ut.valueCopy(json, KName.KEY, KName.DIRECTORY_ID));
                return Ux.future(synced);
            });
    }

    private static Future<JsonArray> mkdir(final JsonArray queueUp, final List<IDirectory> storeList) {
        // 1. 截断返回，若 queueAd 为空，则直接返回
        if (Ut.isNil(queueUp)) {
            return Ux.futureA();
        }


        // 2. 补充构造队列中的 runComponent 来完成整体的同步执行操作
        final ConcurrentMap<String, IDirectory> storeMap = Ut.elementMap(storeList, IDirectory::getStorePath);
        final JsonArray queueRun = new JsonArray();
        Ut.itJArray(queueUp).forEach(json -> {
            final String storePath = json.getString(KName.STORE_PATH);
            if (Ut.isNotNil(storePath)) {
                // 将 IDirectory 中的数据直接拷贝到 json 中
                final IDirectory store = storeMap.get(storePath);
                if (Objects.nonNull(store)) {
                    // 拷贝 directoryId
                    json.put(KName.DIRECTORY_ID, store.getKey());
                    /*
                     * 拷贝访问信息
                     * -- visit ( owner )       访问者
                     * -- visitMode             访问模式：r, w, x
                     * -- visitRole             可访问角色
                     * -- visitGroup            可访问组
                     */
                    json.put(KName.VISIT_MODE, Ut.toJArray(store.getVisitMode()));
                    json.put(KName.VISIT_ROLE, Ut.toJArray(store.getVisitRole()));
                    json.put(KName.VISIT_GROUP, Ut.toJArray(store.getVisitGroup()));
                }
                queueRun.add(json);
            } else {
                Is.LOG.Init.warn(IsStore.class, "runComponent is null and the directory will be ignored, json = {0}", json.encode());
            }
        });
        return Ux.future(queueRun);
    }

    private static Future<JsonArray> mkdir(final JsonArray queueAd, final JsonObject config) {
        // 1. 截断返回，若 queueAd 为空，则直接返回
        if (Ut.isNil(queueAd)) {
            return Ux.futureA();
        }


        // 2. 构造添加队列中的 runComponent
        final JsonArray queueRun = new JsonArray();
        Ut.itJArray(queueAd).forEach(json -> {
            String runComponent = Ut.valueString(json, KName.Component.RUN_COMPONENT);
            if (Ut.isNil(runComponent)) {
                runComponent = Ut.valueString(config, KName.Component.RUN_COMPONENT);
            }
            if (Ut.isNotNil(runComponent)) {
                json.put(KName.Component.RUN_COMPONENT, runComponent);
                queueRun.add(json);
            } else {
                Is.LOG.Init.warn(IsStore.class, "runComponent is null and the directory will be ignored, json = {0}", json.encode());
            }
        });
        return Ux.future(queueRun);
    }
}
