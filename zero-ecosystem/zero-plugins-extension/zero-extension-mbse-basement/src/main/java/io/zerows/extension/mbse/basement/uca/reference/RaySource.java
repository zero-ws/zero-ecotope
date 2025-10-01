package io.zerows.extension.mbse.basement.uca.reference;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.program.Kv;
import io.zerows.epoch.common.shared.reference.RDao;
import io.zerows.epoch.common.shared.reference.RQuote;
import io.zerows.epoch.common.shared.reference.RRule;
import io.zerows.core.constant.KWeb;
import io.zerows.core.fn.FnZero;
import io.zerows.core.web.cache.Rapid;
import io.zerows.specification.modeling.HRecord;
import io.zerows.unity.Ux;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

/**
 * ## Data Fetcher
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class RaySource {
    private transient final RQuote quote;

    private RaySource(final RQuote quote) {
        this.quote = quote;
    }

    static RaySource create(final RQuote quote) {
        return new RaySource(quote);
    }

    /*
     * RaySource
     * field1 -> DataQRule
     * field2 -> DataQRule
     */
    public ConcurrentMap<String, JsonArray> single(final HRecord record) {
        return this.data(rule -> rule.compile(record));
    }

    public Future<ConcurrentMap<String, JsonArray>> singleAsync(final HRecord record) {
        return this.dataAsync(rule -> rule.compile(record));
    }

    /*
     * 批量运算
     */
    public ConcurrentMap<String, JsonArray> batch(final HRecord[] records) {
        return this.data(rule -> rule.compile(records));
    }

    public Future<ConcurrentMap<String, JsonArray>> batchAsync(final HRecord[] records) {
        return this.dataAsync(rule -> rule.compile(records));
    }


    private Future<ConcurrentMap<String, JsonArray>> dataAsync(final Function<RRule, JsonObject> supplier) {
        return this.ready(supplier, (fieldCodes, execMap) -> {
            final ConcurrentMap<Integer, Future<JsonArray>> futureMap = new ConcurrentHashMap<>();
            execMap.forEach((hashCode, kv) -> {
                final JsonObject condition = kv.key();
                final RDao dao = kv.value();
                futureMap.put(hashCode,
                    Rapid.<String, JsonArray>object(KWeb.CACHE.REFERENCE, KWeb.ARGS.V_DATA_EXPIRED)
                        .cached(String.valueOf(hashCode), () -> {
                            LOG.Uca.info(this.getClass(), "Async Batch condition building: {0}", condition.encode());
                            return dao.fetchByAsync(condition);
                        }));
            });
            return FnZero.combineM(futureMap).compose(queriedMap -> {
                final ConcurrentMap<String, JsonArray> data = new ConcurrentHashMap<>();
                queriedMap.forEach((hashCode, dataArray) -> {
                    fieldCodes.forEach((field, codeKey) -> {
                        if (Objects.equals(hashCode, codeKey)) {
                            data.put(field, dataArray);
                        }
                    });
                });
                return Ux.future(data);
            });
        });
    }

    private ConcurrentMap<String, JsonArray> data(final Function<RRule, JsonObject> supplier) {
        return this.ready(supplier, (fieldCodes, execMap) -> {
            final ConcurrentMap<String, JsonArray> data = new ConcurrentHashMap<>();
            execMap.forEach((hashCode, kv) -> {
                final JsonObject condition = kv.key();
                final RDao dao = kv.value();
                final JsonArray queried = dao.fetchBy(condition);
                /* 反向运算 */
                LOG.Uca.info(this.getClass(), "Batch condition building: {0}, size = {1}",
                    condition.encode(), String.valueOf(queried.size()));
                fieldCodes.forEach((field, codeKey) -> {
                    if (Objects.equals(hashCode, codeKey)) {
                        data.put(field, queried);
                    }
                });
            });
            return data;
        });
    }

    private <T> T ready(
        final Function<RRule, JsonObject> supplier,
        final BiFunction<ConcurrentMap<String, Integer>, ConcurrentMap<Integer, Kv<JsonObject, RDao>>, T> executor) {
        /*
         * 换一种算法
         */
        final ConcurrentMap<String, Integer> fieldCodes = new ConcurrentHashMap<>();
        final ConcurrentMap<Integer, Kv<JsonObject, RDao>> execMap = new ConcurrentHashMap<>();
        this.quote.rules().forEach((field, rule) -> {
            /* 条件处理 */
            final JsonObject condition = supplier.apply(rule);
            if (Objects.nonNull(condition)) {
                final int hashCode = condition.hashCode();
                fieldCodes.put(field, hashCode);
                /* 横向压缩 */
                final RDao dao = this.quote.dao(field);
                execMap.put(hashCode, Kv.create(condition, dao));
            }
        });
        return executor.apply(fieldCodes, execMap);
    }
}
