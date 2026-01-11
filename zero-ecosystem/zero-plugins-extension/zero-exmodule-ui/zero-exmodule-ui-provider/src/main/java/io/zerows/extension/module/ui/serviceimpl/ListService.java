package io.zerows.extension.module.ui.serviceimpl;

import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ui.domain.tables.daos.UiListDao;
import io.zerows.extension.module.ui.domain.tables.daos.UiViewDao;
import io.zerows.extension.module.ui.domain.tables.pojos.UiList;
import io.zerows.extension.module.ui.domain.tables.pojos.UiView;
import io.zerows.extension.module.ui.servicespec.ListStub;
import io.zerows.extension.module.ui.servicespec.OptionStub;
import io.zerows.extension.module.ui.spi.QBECache;
import io.zerows.management.OCacheStore;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.specification.atomic.HCombiner;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static io.zerows.extension.module.ui.boot.Ui.LOG;

public class ListService implements ListStub {
    private static final LogOf LOGGER = LogOf.get(ListService.class);
    private static final String FIELD_COMBINER = "classCombiner";
    @Inject
    private transient OptionStub optionStub;

    @Override
    public Future<JsonObject> fetchById(final String listId) {
        /*
         * Read list configuration for configuration
         */
        return DB.on(UiListDao.class).<UiList>fetchByIdAsync(listId).compose(list -> {
            if (Objects.isNull(list)) {
                LOG.Ui.warn(LOGGER, " Form not found, id = {0}", listId);
                return Ux.future(new JsonObject());
            } else {
                /*
                 * It means here are some additional configuration that should be
                 * fetch then
                 */
                final JsonObject listJson = Ut.serializeJson(list);
                return this.attachConfig(listJson);
            }
        });
    }

    @Override
    public Future<JsonArray> fetchByIdentifier(final String identifier, final String sigma) {
        final JsonObject condition = new JsonObject();
        condition.put(KName.IDENTIFIER, identifier);
        condition.put(KName.SIGMA, sigma);
        return DB.on(UiListDao.class).<UiList>fetchAndAsync(condition)
            /* List<UiList> */
            .compose(Ux::futureA);
    }

    @Override
    public Future<JsonArray> fetchQr(final JsonObject condition) {

        final QSorter sorter = QSorter.of(KName.SORT, true);

        return DB.on(UiViewDao.class).<UiView>fetchAsync(condition, sorter)
            // Cached Data for future usage
            .compose(QBECache::cached)
            .compose(Ux::futureA)
            /* List<UiListQr> */
            .compose(Ux.futureF(
                /*
                 * 1. 标准：criteria, projection, rows
                 * 2. 扩展：qrComponent, qrConfig
                 * 3. 安全：view, position
                 * 上述七个字段不出现在返回列表中，在执行Qr时做后端运算，请求时只提供
                 * 当前Qr的名称, Qr存储的名字使用标准的 position / view 的模式，传入时
                 * 执行 Base64 加密，后端可直接解密操作
                 */
                VName.KEY_CRITERIA, VName.KEY_PROJECTION, KName.Rbac.ROWS,
                KName.Component.QR_COMPONENT, KName.Component.QR_CONFIG,
                KName.VIEW, KName.POSITION
            ));
    }

    private Future<JsonObject> attachConfig(final JsonObject listJson) {
        /*
         * Capture important configuration here
         */
        Ut.valueToJObject(listJson,
            ListStub.FIELD_OPTIONS,
            ListStub.FIELD_OPTIONS_AJAX,
            ListStub.FIELD_OPTIONS_SUBMIT,
            ListStub.FIELD_V_SEGMENT
        );
        return Ux.future(listJson)
            /* vQuery */
            .compose(this.ofUi(ListStub.FIELD_V_QUERY, this.optionStub::fetchQuery))
            /* vSearch */
            .compose(this.ofUi(ListStub.FIELD_V_SEARCH, this.optionStub::fetchSearch))
            /* vTable */
            .compose(this.ofUi(ListStub.FIELD_V_TABLE, this.optionStub::fetchTable))
            /* vSegment */
            .compose(this.ofTree(ListStub.FIELD_V_SEGMENT, false, this.optionStub::fetchFragment))
            /* Combiner for final processing */
            .compose(this::ofCombiner);
    }

    @SuppressWarnings("unchecked")
    private Future<JsonObject> ofCombiner(final JsonObject json) {
        if (Ut.isNil(json) || !json.containsKey(FIELD_COMBINER) || Ut.isNil(FIELD_COMBINER)) {
            return Future.succeededFuture(json);
        }
        // Class<?>
        final Class<?> clazz = Ut.valueC(json, FIELD_COMBINER, null);
        if (Objects.isNull(clazz)) {
            return Future.succeededFuture(json);
        }
        final HCombiner<JsonObject> combiner = (HCombiner<JsonObject>) OCacheStore.CC_COMBINER.pick(() -> Ut.instance(clazz), clazz.getName());
        return combiner.executeAsync(json);
    }

    @SuppressWarnings("all")
    private <T> Function<JsonObject, Future<JsonObject>> ofUi(final String field, final Function<T, Future<JsonObject>> executor) {
        return mount -> {
            if (Ut.isNil(field) ||
                !mount.containsKey(field) ||
                Objects.isNull(executor)) {
                // Nothing
                return Future.succeededFuture(mount);
            }
            final T value = (T) mount.getValue(field);
            if (Objects.isNull(value)) {
                // Nothing
                return Future.succeededFuture(mount);
            }
            // Function Processing
            return executor.apply(value).compose(data -> {
                if (Ut.isNotNil(data)) {
                    mount.put(field, data);
                }
                return Future.succeededFuture(mount);
            });
        };
    }

    /*
     * mount
     * {
     *     "a": "Tool",
     *     "e": "Tool"
     * }
     * ---> 所有的 Tool 类型的节点都会被处理掉并实现替换
     */
    @SuppressWarnings("all")
    private <T> Function<JsonObject, Future<JsonObject>> ofTree(
        final String field, final boolean deeply, final Function<T, Future<JsonObject>> executor) {
        return mount -> {
            if (Ut.isNil(field) ||
                !mount.containsKey(field) ||
                Objects.isNull(executor)) {
                // Nothing
                return Future.succeededFuture(mount);
            }
            // 切换算法处理，执行 Map 操作
            final Object vSegment = mount.getValue(field);
            if (vSegment instanceof final JsonObject tree) {
                return this.ofTree(tree, deeply, executor).compose(segmentData -> {
                    if (Ut.isNotNil(segmentData)) {
                        mount.put(field, segmentData);
                    }
                    return Future.succeededFuture(mount);
                });
            } else {
                return Future.succeededFuture(mount);
            }
        };
    }

    /*
     * 将树上所有节点的：field = Tool 转换成 field = Tool -> JsonObject，且支持
     * 递归和异步，属于深度计算算法，比原始的只处理两级层级更深，递归终止条件为原始数据不存在 JsonObject 节点
     * 注意递归终止是处理原始数据，这种计算是从配置树到数据树的一种转换
     */
    @SuppressWarnings("all")
    private <T> Future<JsonObject> ofTree(
        final JsonObject input, final boolean deeply, final Function<T, Future<JsonObject>> executor) {
        final ConcurrentMap<String, Future<JsonObject>> tree = new ConcurrentHashMap<>();
        final ConcurrentMap<String, Future<JsonObject>> children = new ConcurrentHashMap<>();
        for (final String field : input.fieldNames()) {
            // JsonArray 跳过不执行
            final Object value = input.getValue(field);
            if (value instanceof final JsonObject json) {
                // 遇到 JsonObject 节点，执行子提取，递归
                if (deeply) {
                    children.put(field, ofTree(json, true, executor));
                }
            } else {
                T cast = null;
                try {
                    cast = (T) input.getValue(field);
                } catch (final Throwable ex) {
                }
                if (Objects.nonNull(cast)) {
                    tree.put(field, executor.apply(cast));
                }
            }
        }
        return FnVertx.combineM(tree).compose(treeMap -> {
            final JsonObject treeData = Ut.toJObject(treeMap);
            input.mergeIn(treeData, true);
            return Future.succeededFuture(input);
        }).compose(response -> FnVertx.combineM(children).compose(childMap -> {
            final JsonObject childData = Ut.toJObject(childMap);
            response.mergeIn(childData, true);
            return Future.succeededFuture(response);
        }));
    }
}
