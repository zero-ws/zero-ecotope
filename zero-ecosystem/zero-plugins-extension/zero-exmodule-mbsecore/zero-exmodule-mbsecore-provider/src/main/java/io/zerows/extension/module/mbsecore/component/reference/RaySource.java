package io.zerows.extension.module.mbsecore.component.reference;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.metadata.RDao;
import io.zerows.platform.metadata.RQuote;
import io.zerows.platform.metadata.RRule;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ## Data Fetcher
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
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
                futureMap.put(hashCode, HMM.<String, JsonArray>of(KWeb.CACHE.REFERENCE).cached(
                    String.valueOf(hashCode),
                    () -> {
                        log.info("[ ZERO ] ( UCA ) 异步批量查询：{}", condition.encode());
                        return dao.fetchByAsync(condition);
                    },
                    KWeb.ARGS.V_DATA_EXPIRED
                ));
            });
            return Fx.combineM(futureMap).compose(queriedMap -> {
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
                log.info("[ ZERO ] ( UCA ) 同步批量查询：{}，数量：{}", condition.encode(), queried.size());
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
