package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.vertx.ext.auth.User;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.web.cache.shared.UxPool;
import io.zerows.epoch.database.cp.DataPool;
import io.zerows.epoch.database.jooq.operation.UxJoin;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.epoch.metadata.security.TokenJwt;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.metadata.KIntegration;
import io.zerows.platform.metadata.Kv;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * #「Kt」Utility X Component in zero
 * <p>
 * Here Ux is a util interface of uniform to call different tools.
 * It just like helper for income usage.
 */
@SuppressWarnings("all")
public final class Ux extends _Where {

    public static <T> Future<T> future(final T entity) {
        return ToCommon.future(entity);
    }

    public static <T> Future<T> future(final T input, final List<Function<T, Future<T>>> functions) {
        return Async.future(input, functions);
    }

    public static <T> Future<T> future(final T input, final Set<Function<T, Future<T>>> functions) {
        return Async.future(input, functions);
    }

    public static <T> Future<T> future() {
        return ToCommon.future(null);
    }

    /*
     *  future prefix processing here
     *
     * JsonArray
     * 1) futureA
     * -- futureA()
     * -- futureA(List)
     * -- futureA(List, pojo)
     * -- futureA(String)
     * -- futureA(Record[])
     *
     * JsonObject
     * 2) futureJ
     * -- futureJ()
     * -- futureJ(Tool)
     * -- futureJ(Tool, pojo)
     * -- futureJ(String)
     * -- futureJ(Record)
     * -- futureJM(Tool, String)
     *
     * List
     * 3) futureL
     * -- futureL()
     * -- futureL(List)
     * -- futureL(List, pojo)
     * -- futureL(String)
     *
     * Grouped
     * 4) futureG
     * -- futureG(List, String)
     * -- futureG(Tool, String)
     * -- futureG(List)
     * -- futureG(Tool)
     *
     * Error Future
     * 5) futureE
     * -- futureE(Tool)
     * -- futureE(Supplier)
     *
     * New Api for normalized mount
     * 6) futureN
     * -- futureN(JsonObject, JsonObject)
     * -- futureN(JsonArray, JsonArray)
     * -- futureN(JsonArray, JsonArray, String)
     * > N for normalize and add new field:  __data,  __flag instead of original data
     *
     * Combine JsonObject and JsonArray by index
     * 7) futureC
     *
     * Filter JsonObject and JsonArray
     * 8) futureF
     * -- futureF(String...)
     * -- futureF(Set<String>)
     * -- futureF(ClustSerializble, String...)
     * -- futureF(JsonArray, String...)
     * -- futureF(JsonObject, Set<String>)
     * -- futureF(JsonArray, Set<String>)
     */
    // ----------------------- futureF ----------------------

    public static <T extends ClusterSerializable> Function<T, Future<T>> futureF(final Set<String> removed) {
        return input -> (input instanceof JsonObject) ?
            futureF((JsonObject) input, removed).compose(json -> ToCommon.future((T) json)) :
            futureF((JsonArray) input, removed).compose(array -> ToCommon.future((T) array));
    }

    public static <T extends ClusterSerializable> Function<T, Future<T>> futureF(final String... removed) {
        return futureF(Arrays.stream(removed).collect(Collectors.toSet()));
    }

    public static Future<JsonObject> futureF(final JsonObject input, final String... removed) {
        return ToCommon.future(ToCommon.subset(input, Arrays.stream(removed).collect(Collectors.toSet())));
    }

    public static Future<JsonObject> futureF(final JsonObject input, final Set<String> removed) {
        return ToCommon.future(ToCommon.subset(input, removed));
    }

    public static Future<JsonArray> futureF(final JsonArray input, final String... removed) {
        return ToCommon.future(ToCommon.subset(input, Arrays.stream(removed).collect(Collectors.toSet())));
    }

    public static Future<JsonArray> futureF(final JsonArray input, final Set<String> removed) {
        return ToCommon.future(ToCommon.subset(input, removed));
    }

    // ----------------------- futureN ----------------------
    public static Future<JsonObject> futureN(final JsonObject input, final JsonObject previous, final JsonObject current) {
        return ServiceNorm.effect(input, previous, current);
    }

    public static Future<JsonArray> futureN(final JsonArray previous, final JsonArray current) {
        return ServiceNorm.effect(previous, current, KName.KEY);
    }

    public static Future<JsonArray> futureN(final JsonArray previous, final JsonArray current, final String field) {
        return ServiceNorm.effect(previous, current, field);
    }

    public static <T> Future<T> futureC(final T input, final T processed) {
        return ServiceNorm.combine(input, processed);
    }

    public static Future<Boolean> futureT() {
        return ToCommon.future(Boolean.TRUE);
    }

    public static <T> Future<Boolean> futureT(final T input) {
        return ToCommon.future(Boolean.TRUE);
    }

    public static Future<Boolean> futureF() {
        return ToCommon.future(Boolean.FALSE);
    }

    public static <T> Future<List<T>> futureL() {
        return future(new ArrayList<>());
    }

    public static <T> Future<List<T>> futureL(final T single) {
        final List<T> list = new ArrayList<>();
        list.add(single);
        return future(list);
    }

    public static <T> Function<Throwable, Future<T>> futureE(final T input) {
        return Async.toErrorFuture(() -> input);
    }

    public static <T> Function<Throwable, Future<T>> futureE(final Supplier<T> supplier) {
        return Async.toErrorFuture(supplier);
    }

    public static <T> Future<JsonArray> futureA(final List<T> list, final String pojo) {
        return Future.succeededFuture(Ut.toJson(list, pojo));
    }

    public static Future<JsonArray> futureA() {
        return futureA(new ArrayList<>(), VString.EMPTY);
    }

    public static Future<JsonArray> futureA(Throwable ex) {
        return Async.<JsonArray>toErrorFuture(JsonArray::new).apply(ex);
    }

    public static <T> Future<JsonArray> futureA(final List<T> list) {
        return futureA(list, VString.EMPTY);
    }

    public static <T> Function<List<T>, Future<JsonArray>> futureA(final String pojo) {
        return list -> futureA(list, pojo);
    }

    // --------------- Tool of entity processing -----------------

    public static <T> Future<JsonObject> futureJ(final T entity, final String pojo) {
        return Future.succeededFuture(Ut.toJson(entity, pojo));
    }

    public static <T, R> Function<List<R>, Future<JsonObject>> futureJM(final T entity, final String field) {
        return list -> Future.succeededFuture(ToCommon.toMerge(entity, field, list));
    }

    public static Future<JsonObject> futureJ() {
        return futureJ(new JsonObject(), VString.EMPTY);
    }

    public static Future<JsonObject> futureJ(Throwable ex) {
        return Async.<JsonObject>toErrorFuture(JsonObject::new).apply(ex);
    }

    public static <T> Future<JsonObject> futureJ(final T entity) {
        return futureJ(entity, VString.EMPTY);
    }

    public static <T> Function<T, Future<JsonObject>> futureJ(final String pojo) {
        return entity -> futureJ(entity, pojo);
    }

    // --------------- List<Tool> of future processing -----------------
    public static <T> Future<List<JsonObject>> futureL(final List<T> list, final String pojo) {
        return Future.succeededFuture(ToCommon.toJList(list, pojo));
    }

    public static <T> Future<List<JsonObject>> futureLJ() {
        return futureL(new ArrayList<>(), VString.EMPTY);
    }

    public static <T> Future<List<JsonObject>> futureL(final List<T> list) {
        return futureL(list, VString.EMPTY);
    }

    public static <T> Function<List<T>, Future<List<JsonObject>>> futureL(final String pojo) {
        return list -> futureL(list, pojo);
    }

    // --------------- Record processing -----------------

    public static Future<JsonObject> futureJ(final HRecord record) {
        return ToCommon.future(record.toJson());
    }

    public static Future<JsonArray> futureA(final HRecord[] records) {
        return ToCommon.future(Ut.toJArray(records));
    }

    // --------------- Future of Map -----------------
    public static <T> Future<ConcurrentMap<String, JsonArray>> futureG(final List<T> item, final String field) {
        return futureG(Ut.toJson(item, ""), field);
    }

    public static Future<ConcurrentMap<String, JsonArray>> futureG(final JsonArray item, final String field) {
        return Future.succeededFuture(Ut.elementGroup(item, field));
    }

    public static <T> Future<ConcurrentMap<String, JsonArray>> futureG(final List<T> item) {
        return futureG(Ut.toJson(item, ""), "type");
    }

    public static Future<ConcurrentMap<String, JsonArray>> futureG(final JsonArray item) {
        return futureG(item, "type");
    }

    /*
     * key part for extract data from environment
     */
    public static String keyUser(final User user) {
        return TokenJwt.of(user).user();
    }

    // ---------------------------------- Children Utility

    /**
     * Inner class of `Jooq` tool of Jooq Engine operations based on pojo here.
     * When developers want to access database and select zero default implementation.
     *
     * @author lang
     * <pre><code>
     *
     *     public Future<JsonObject> fetchAsync(final User user){
     *         return Ux.Jooq.on(UserDao.class)
     *                    .insertAsync(user)
     *                    .compose(Ux::fnJObject);
     *     }
     *
     * </code></pre>
     * <p>
     * Here you can do database access smartly and do nothing then.
     */
    public static class Jooq {

        /**
         * 专用于新配置下的提取 UxJooq 的方法
         *
         * @param connect 连接配置，对应 model/connect.yml
         *
         * @return 返回引用对象
         */
        public static UxJooq bridge(final MDConnect connect) {
            final Class<?> daoCls = connect.getDao();
            Objects.requireNonNull(daoCls);
            // Pojo with
            final String pojoFile = connect.getPojoFile();
            if (Ut.isNil(pojoFile)) {
                return on(daoCls);
            } else {
                return on(daoCls).on(pojoFile);
            }
        }

        /**
         * Get reference of UxJooq that bind to Dao class, this method won't access standard database,
         * instead it will access history database that has been configured in `vertx-jooq.yml` file.
         * <p>
         * key = orbit
         *
         * <pre><code>
         *
         * jooq:
         *   orbit:
         *     driverClassName: "com.mysql.cj.jdbc.Driver"
         *     ......
         *
         * </code></pre>
         *
         * @param clazz The class of `VertxDao` that has been generated by jooq tool
         *
         * @return UxJooq reference that has been initialized
         */
        public static UxJooq ons(final Class<?> clazz) {
            return UxJooq.ofHistory(clazz);
        }

        /**
         * Get reference of UxJooq that bind to Dao class, this method access standard database,
         * the configured position is `vertx-jooq.yml`
         * <p>
         * key = provider
         *
         * <pre><code>
         * jooq:
         *   provider:
         *     driverClassName: "com.mysql.cj.jdbc.Driver"
         * </code></pre>
         *
         * @param clazz The class of `VertxDao` that has been generated by jooq tool
         *
         * @return UxJooq reference that has been initialized
         */
        public static UxJooq on(final Class<?> clazz) {
            return UxJooq.of(clazz);
        }

        /**
         * The overloading method of above `on(Class<?>)` method here.
         *
         * @param clazz The class of `VertxDao` that has been generated by jooq tool
         * @param pool  Input data pool reference, it provide developers to access other database in one application.
         *
         * @return UxJooq reference that has been initialized
         */
        public static UxJooq on(final Class<?> clazz, final DataPool pool) {
            return UxJooq.of(clazz, pool);
        }

        /**
         * The overloading method of above `on(Class<?>)` method here.
         *
         * @param clazz The class of `VertxDao` that has been generated by jooq tool
         * @param key   the key configuration in vertx-jooq.yml such as above "orbit", "provider"
         *
         * @return UxJooq reference that has been initialized
         */
        public static UxJooq on(final Class<?> clazz, final String key) {
            return UxJooq.of(clazz, key);
        }
    }

    // -> Jooq -> Multi
    public static class Join {

        public static UxJoin bridge(final MDConnect active, final MDConnect standBy,
                                    final Kv<String, String> fieldJoin, final JsonObject aliasJ) {
            final UxJoin join = UxJoin.of(null);
            final String pojoActive = active.getPojoFile();
            if (Ut.isNotNil(pojoActive)) {
                join.pojo(active.getDao(), pojoActive);
            }
            final String pojoStandBy = standBy.getPojoFile();
            if (Ut.isNotNil(pojoStandBy)) {
                join.pojo(standBy.getDao(), pojoStandBy);
            }
            final String fieldActive = Ut.isNotNil(fieldJoin.key()) ? fieldJoin.key() : KName.KEY;
            final String fieldStandBy = Ut.isNotNil(fieldJoin.value()) ? fieldJoin.value() : KName.KEY;

            join.add(active.getDao(), fieldActive).join(standBy.getDao(), fieldStandBy);

            return bridgeAlias(join, active, standBy, aliasJ);
        }

        public static UxJoin bridge(final MDConnect active, final MDConnect standBy,
                                    final Kv<String, String> fieldJoin) {
            return bridge(active, standBy, fieldJoin, null);
        }

        /**
         * alias 的数据结构如
         * <pre><code>
         *     "alias": {
         *          "{TABLE1}": [
         *              field1,
         *              field2,
         *          ],
         *          "{TABLE2}": [
         *              field1,
         *              field2
         *          ]
         *     }
         * </code></pre>
         *
         * @param join
         * @param active
         * @param standBy
         * @param aliasJ
         */
        public static UxJoin bridgeAlias(final UxJoin join, final MDConnect active, final MDConnect standBy, final JsonObject aliasJ) {
            if (Ut.isNil(aliasJ)) {
                return join;
            }
            Ut.<JsonArray>itJObject(aliasJ).forEach(entry -> {
                final JsonArray fields = entry.getValue();
                if (2 == fields.size()) {
                    final String tableName = entry.getKey();
                    final Class<?> daoCls;
                    if (tableName.equals(active.getTable())) {
                        daoCls = active.getDao();
                    } else if (tableName.equals(standBy.getTable())) {
                        daoCls = standBy.getDao();
                    } else {
                        Ut.Log.database(UxJoin.class).error("( Join ) Please check your table name: {}", tableName);
                        daoCls = null;
                    }
                    final String fieldKey = fields.getString(VValue.IDX);
                    final String fieldJoin = fields.getString(VValue.ONE);
                    join.alias(daoCls, fieldKey, fieldJoin);
                } else {
                    Ut.Log.database(UxJoin.class).error("( Join ) Please check your alias configuration: {}", fields);
                }
            });
            return join;
        }

        public static UxJoin on(final String configFile) {
            return UxJoin.of(configFile);
        }

        public static UxJoin on() {
            return UxJoin.of(null);
        }

        public static UxJoin on(final Class<?> daoCls) {
            return UxJoin.of(null).add(daoCls);
        }
    }

    public static class Pool {

        public static UxPool on(final String name) {
            return UxPool.of(name);
        }

        public static UxPool on() {
            return UxPool.of(null);
        }
    }

    public static class Job {
        public static UxJob on() {
            return new UxJob();
        }
    }

    public static class Ldap {
        public static UxLdap on(final KIntegration integration) {
            return CACHE.CC_LDAP.pick(() -> new UxLdap(integration), String.valueOf(integration.hashCode()));
        }
    }

    /*
     * Here the Jwt class is for lagency system because all following method will be called and existed
     * But the new structure is just like following:
     *
     * 1. Lee -> ( Impl ) by Service Loader
     * 2. The `AuthWall.JWT` will be selected and called API of Lee interface
     * 3. The final result is token ( encoding / decoding ) part
     * 4. The implementation class is defined in `zero-ifx-auth` instead of standard framework
     *
     * If you want to use security module, you should set-up `zero-ifx-auth` infix instead, or
     * you can run zero framework in non-secure mode
     */
    public static class Jwt {

        public static String token(final JsonObject data) {
            return TokenJwt.of(data).token();
        }

        public static JsonObject extract(final String token) {
            return TokenJwt.of(token).data();
        }
    }

}
