package io.zerows.program;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Account;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.SecurityType;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static <T> Future<JsonArray> futureA(final List<T> list, final String pojo) {
        return Future.succeededFuture(Ut.toJson(list, pojo));
    }

    public static Future<JsonArray> futureA() {
        return futureA(new ArrayList<>(), VString.EMPTY);
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


    // --------------- Future of Map -----------------
    public static String userId(final User user) {
        return Account.userId(user);
    }

    // NEW BEGIN -------------------------------------------------
    // 新版用户相关的 API
    public static UserAt userAt() {
        return Account.userAt();
    }

    public static UserAt userAt(final User user) {
        return Account.userAt(user);
    }

    public static UserContext userContext() {
        return Account.userContext();
    }

    public static JsonObject userData(final Credentials credentials) {
        return Account.userData(credentials);
    }

    public static JsonObject userToken(final String token) {
        return Account.userToken(token);
    }

    public static JsonObject userToken(final String token, final SecurityType type) {
        return Account.userToken(token, type);
    }

    public static String userToken(final JsonObject tokenData) {
        return Account.userToken(tokenData);
    }

    public static String userToken(final String token, final String field) {
        return Account.userToken(token, field);
    }

    public static User userVx(final UserAt userAt) {
        return Account.userVx(userAt);
    }

    public static User userVx(final User user, final Session session) {
        return Account.userVx(user, session);
    }

    public static String userId() {
        return Account.userId(false);
    }

    public static <T> T userId(final boolean isUuid) {
        return Account.userId(isUuid);
    }

    // 暴露异步方法
    public static <T> Future<T> waitAsync(Supplier<T> executor) {
        return Task.async(executor);
    }

    public static <T> Future<T> waitVirtual(Supplier<T> executor) {
        return Task.asyncVirtual(executor);
    }

    // NEW END -------------------------------------------------
    public static class Job {
        public static UxJob on() {
            return new UxJob();
        }
    }
}
