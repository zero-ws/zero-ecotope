package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.modulat.common.BkConstant;
import io.zerows.extension.module.modulat.domain.tables.daos.BBagDao;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBlock;
import io.zerows.extension.module.modulat.serviceimpl.BagArgService;
import io.zerows.extension.module.modulat.servicespec.BagArgStub;
import io.zerows.extension.skeleton.common.enums.TypeBag;
import io.zerows.platform.enums.modeling.EmModel;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class EquipForBase implements EquipFor {

    protected JsonObject buildQr(final String value, final EmModel.By by) {
        Objects.requireNonNull(by, "[ XMOD ] 查询维度 by 不可为空");
        // 抽取基础判断逻辑
        final JsonObject conditionJ = by.whereBy(value);
        final Set<String> bagNames = Set.of(
            TypeBag.FOUNDATION,
            TypeBag.COMMERCE,
            TypeBag.EXTENSION
        ).stream().map(TypeBag::key).collect(Collectors.toSet());
        conditionJ.put(KName.TYPE + ",i", Ut.toJArray(bagNames));
        return conditionJ;
    }

    protected Future<Map<String, List<BBag>>> fetchBags(final JsonObject condition) {
        this.log().info("{} 查询所有功能包：{}", BkConstant.K_PREFIX, condition);
        return DB.on(BBagDao.class).<BBag>fetchAsync(condition).compose(bags -> {
            // 无值空返回
            if (Objects.isNull(bags) || bags.isEmpty()) {
                return Future.succeededFuture(new ConcurrentHashMap<>());
            }
            // 先正向分组提取 bagId -> store 的映射关系，store 必须有值
            final List<BBag> bagList = new ArrayList<>(bags);
            final ConcurrentMap<String, String> storeMap = new ConcurrentHashMap<>();
            bags.forEach(bag -> {
                final String store = this.lookupStore(bag, bagList);
                if (Ut.isNotNil(store)) {
                    storeMap.put(bag.getId(), store);
                }
            });


            // 逆向构造
            final Map<String, List<BBag>> revertMap = new ConcurrentHashMap<>();
            storeMap.forEach((bagId, store) -> {
                final BBag bag = bagList.stream()
                    .filter(item -> bagId.equals(item.getId()))
                    .findFirst().orElse(null);
                if (Objects.nonNull(bag)) {
                    revertMap.computeIfAbsent(store, key -> new ArrayList<>()).add(bag);
                }
            });
            return Future.succeededFuture(revertMap);
        });
    }

    private String lookupStore(final BBag bag, final List<BBag> bagList) {
        String store = bag.getStore();
        if (Ut.isNil(store) && Ut.isNotNil(bag.getParentId())) {
            final BBag parent = bagList.stream()
                .filter(item -> bag.getParentId().equals(item.getId()))
                .findFirst().orElse(null);
            if (Objects.nonNull(parent)) {
                store = this.lookupStore(parent, bagList);
            }
        }
        return store;
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    private Future<JsonObject> dataAsync(final BBag bag, final boolean isOpen) {
        final BagArgStub stub = Ut.singleton(BagArgService.class);
        return stub.seekBlocks(bag).compose(blocks -> {
            final Combiner<BBag, List<BBlock>> combiner = Combiner.forBlock();
            return combiner.configure(bag, blocks);
        }).map(data -> {
            final JsonObject dataJ = data.copy();
            if (isOpen) {
                final Set<String> openSet = this.dataOpen(bag);
                final Set<String> dataKeys = dataJ.fieldNames();
                final Set<String> removeKeys = Ut.elementDiff(dataKeys, openSet);
                removeKeys.forEach(dataJ::remove);
            }
            return dataJ;
        });
    }

    private Set<String> dataOpen(final BBag bag) {
        final JsonArray uiOpen = bag.getUiOpen();
        final Set<String> openSet = Ut.toSet(bag.getUiOpen());
        openSet.add(KName.__.METADATA);
        openSet.add(KName.KEY_P);
        return openSet;
    }

    private Future<JsonObject> dataAsync(final List<BBag> bagList, final boolean isOpen) {
        final List<Future<JsonObject>> futureL = new ArrayList<>();
        bagList.forEach(bag -> futureL.add(this.dataAsync(bag, isOpen)));
        return Fx.combineT(futureL).compose(dataStore -> {
            // 值合并
            final JsonObject combineJ = new JsonObject();
            dataStore.forEach(item -> combineJ.mergeIn(item, true));
            return Future.succeededFuture(combineJ);
        });
    }

    protected Future<JsonObject> dataAsync(final Map<String, List<BBag>> bagMap, final boolean isOpen) {
        final ConcurrentMap<String, Future<JsonObject>> futureMap = new ConcurrentHashMap<>();
        bagMap.forEach((store, bagList) -> futureMap.put(store, this.dataAsync(bagList, isOpen)));
        return Fx.combineM(futureMap).map(stored -> {
            final JsonObject result = new JsonObject();
            stored.forEach(result::put);
            return result;
        });
    }
}
