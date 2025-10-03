package io.zerows.extension.commerce.finance.agent.service.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.KRef;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.extension.commerce.finance.atom.TranData;
import io.zerows.extension.commerce.finance.domain.tables.daos.*;
import io.zerows.extension.commerce.finance.domain.tables.pojos.*;
import io.zerows.extension.commerce.finance.eon.FmConstant;
import io.zerows.extension.commerce.finance.eon.em.EmTran;
import io.zerows.extension.commerce.finance.uca.replica.IkWay;
import io.zerows.extension.commerce.finance.uca.trans.Trade;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-01-24
 */
public class TransService implements TransStub {
    @Override
    public Future<FTrans> createBySettlement(final JsonObject data, final FSettlement settlement) {
        final JsonObject params = new JsonObject();
        {
            params.put(KName.COMMENT, settlement.getComment());
            params.put(KName.TYPE, EmTran.Type.SETTLEMENT.name());
            params.put(KName.KEYS, new JsonArray().add(settlement.getKey()));
        }
        // 1. 构造 FTrans
        return Trade.step06T().flatter(data, List.of(settlement))
            // 2. 构造 FTransOf
            .compose(trans -> this.createTransItem(trans, data, params));
    }

    @Override
    public Future<FTrans> createBySettlement(final JsonObject data, final List<FSettlement> settlements) {
        final JsonObject params = new JsonObject();
        {
            params.put(KName.COMMENT, Ut.valueString(data, KName.COMMENT));
            params.put(KName.TYPE, EmTran.Type.SETTLEMENT.name());
            final JsonArray keys = Ut.toJArray(Ut.valueSetString(settlements, FSettlement::getKey));
            params.put(KName.KEYS, keys);
        }
        // 1. 构造 FTrans
        return Trade.step06T().flatter(data, settlements)
            // 2. 构造 FTransOf
            .compose(trans -> this.createTransItem(trans, data, params));
    }

    @Override
    public Future<FTrans> createByDebt(final JsonObject data, final List<FDebt> debts) {
        final JsonObject params = new JsonObject();
        {
            // 这种模式下前端已经传入了 type 信息
            params.put(KName.COMMENT, Ut.valueString(data, KName.COMMENT));
            params.put(KName.TYPE, Ut.valueString(data, KName.TYPE));
            final JsonArray keys = Ut.toJArray(Ut.valueSetString(debts, FDebt::getKey));
            params.put(KName.KEYS, keys);
        }
        // 1. 构造 FTrans
        return Trade.step07T().flatter(data, debts)
            // 2. 构造 FTransOf
            .compose(trans -> this.createTransItem(trans, data, params));
    }

    private Future<FTrans> createTransItem(
        final FTrans trans, final JsonObject data, final JsonObject params) {
        return Trade.step06TO().scatter(params, trans)
            .compose(nil -> {
                final JsonArray paymentJ = Ut.valueJArray(data, FmConstant.ID.PAYMENT);
                final List<FTransItem> payments = Ux.fromJson(paymentJ, FTransItem.class);

                IkWay.ofT2TI().transfer(trans, payments);
                // 防重复创建：Duplicate entry 'Cash' for key 'name_UNIQUE'
                payments.forEach(payment -> payment.setKey(null));

                return Ux.Jooq.on(FTransItemDao.class).insertAsync(payments);
            })
            .compose(nil -> Ux.future(trans));
    }

    /**
     * 批量查询（只能查询单独类型的数据），查询的最终数据结构如下
     * <pre><code>
     *     [
     *         {
     *             "....": "....",
     *             "items": [],
     *             "of": []
     *         }
     *     ]
     * </code></pre>
     *
     * @param keys    关联主键集合
     * @param typeSet 关联类型
     *
     * @return 关联数据集合
     */
    @Override
    public Future<List<TranData>> fetchAsync(final Set<String> keys,
                                             final Set<EmTran.Type> typeSet) {
        final Set<String> transId = new HashSet<>();
        final KRef itemRef = new KRef();
        final ConcurrentMap<String, List<FTransOf>> tranMap = new ConcurrentHashMap<>();
        // WHERE OBJECT_TYPE = ? AND OBJECT_ID = ?
        final JsonObject condition = Ux.whereAnd();
        condition.put("objectType,i", Ut.toJArray(typeSet.stream()
            .map(EmTran.Type::name).collect(Collectors.toSet())
        ));
        condition.put("objectId,i", Ut.toJArray(keys));
        // WHERE OBJECT_TYPE = ? AND OBJECT_ID = ?
        return Ux.Jooq.on(FTransOfDao.class).<FTransOf>fetchAsync(condition)
            .compose(transOf -> {
                tranMap.putAll(Ut.elementGroup(transOf, FTransOf::getTransId));
                /*
                 * 此处 TransOf 的目的是提取 transIds，最终的数据结构是 JsonArray 的结构，每一个元素都是一个
                 * Trans 对象，然后在对象之下紧跟 items 来表示 transId 对应的所有 items 数据。
                 */
                transId.addAll(Ut.valueSetString(transOf, FTransOf::getTransId));
                // 跳过选择，直接查询 FTransItem
                final JsonObject condTrans = Ux.whereAnd();
                condTrans.put("transactionId,i", Ut.toJArray(transId));
                return Ux.Jooq.on(FTransItemDao.class).<FTransItem>fetchAsync(condTrans);
            })
            .compose(itemRef::future)
            .compose(items -> Ux.Jooq.on(FTransDao.class).<FTrans>fetchInAsync(KName.KEY, Ut.toJArray(transId)))
            .compose(transList -> {
                final List<FTransItem> items = itemRef.get();
                final ConcurrentMap<String, List<FTransItem>> grouped = Ut.elementGroup(items, FTransItem::getTransactionId);
                final List<TranData> response = new ArrayList<>();
                transList.forEach(tran -> {
                    final TranData object = TranData.instance().transaction(tran);
                    final List<FTransItem> itemData = grouped.getOrDefault(tran.getKey(), new ArrayList<>());
                    final List<FTransOf> ofData = tranMap.getOrDefault(tran.getKey(), new ArrayList<>());
                    response.add(object.items(itemData).of(ofData));
                });
                return Ux.future(response);
            });
    }

    @Override
    public Future<JsonObject> fetchAsync(final String key) {
        // 单条交易记录
        final JsonObject response = new JsonObject();
        return Ux.Jooq.on(FTransDao.class).<FTrans>fetchByIdAsync(key)
            .compose(trans -> {
                response.mergeIn(Ux.toJson(trans));
                return this.fetchRelated(key);
            })
            .compose(relatedMap -> {
                relatedMap.forEach(response::put);                  // debts, settlements
                return Ux.Jooq.on(FSettlementItemDao.class)
                    .<FSettlementItem>fetchAsync("finishedId", key);
            })
            .compose(items -> {
                response.put(KName.ITEMS, Ux.toJson(items));        // items
                return Ux.Jooq.on(FTransItemDao.class)
                    .<FTransItem>fetchAsync("transactionId", key);
            })
            .compose(payment -> {
                response.put(FmConstant.ID.PAYMENT, Ux.toJson(payment));  // payment
                return Ux.future(response);
            });
    }

    /*
     * 抓取 debts, settlements
     */
    private Future<ConcurrentMap<String, JsonArray>> fetchRelated(final String key) {
        return Ux.Jooq.on(FTransOfDao.class).<FTransOf>fetchAsync("transId", key).compose(transOfs -> {
            final ConcurrentMap<String, Future<JsonArray>> futureMap = new ConcurrentHashMap<>();
            final Set<String> keySettle = new HashSet<>();
            final Set<String> keyDebt = new HashSet<>();
            transOfs.stream()
                // 防止 null 过滤
                .filter(Objects::nonNull)
                .filter(transOf -> Ut.isNotNil(transOf.getObjectType()))
                .filter(transOf -> Ut.isNotNil(transOf.getObjectId()))
                .forEach(transOf -> {
                    final String type = transOf.getObjectType();
                    if (EmTran.Type.SETTLEMENT.name().equals(type)) {
                        keySettle.add(transOf.getObjectId());
                    } else {
                        // DEBT / REFUND
                        keyDebt.add(transOf.getObjectId());
                    }
                });


            /*
             * debts = JsonArray
             * settlements = JsonArray
             */
            futureMap.put(KName.Finance.SETTLEMENTS, Ux.Jooq.on(FSettlementDao.class)
                .fetchJInAsync(KName.KEY, Ut.toJArray(keySettle))
            );
            futureMap.put(KName.Finance.DEBTS, Ux.Jooq.on(FDebtDao.class)
                .fetchJInAsync(KName.KEY, Ut.toJArray(keyDebt))
            );
            return Fx.combineM(futureMap);
        });
    }
}
