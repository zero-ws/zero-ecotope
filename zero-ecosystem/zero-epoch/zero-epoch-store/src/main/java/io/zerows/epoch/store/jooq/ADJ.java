package io.zerows.epoch.store.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.dbe.jooq.spi.LoadREF;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.jooq.DBJx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.platform.constant.VString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 统一构造流程（双流程）
 * <pre>
 *    1. 使用 {@link Join} 构造 {@link ADJ}
 *       -> 分析初始化 {@link LoadREF}
 *          -> 调用 {@link DBLoad} 来构造 {@link DBNode}
 *             -> 根据 {@link DBNode} 构造 {@link DBRef}
 *       -> {@link DBRef} 构造完成
 *    2. 使用 {@link DBRef} 构造 {@link ADJ}
 *    3. 最终的流程合二为一
 * </pre>
 */
@Slf4j
public final class ADJ {
    private static final Cc<String, ADJ> CC_ADB = Cc.openThread();

    private final DBJx dbj;

    private ADJ(final DBRef ref, final DBS dbs) {
        this.dbj = DBJx.of(ref, dbs);
    }

    private ADJ(final Join join, final DBS dbs) {
        this.dbj = DBJx.of(join, dbs);
    }

    static ADJ of(final DBRef ref, final DBS dbs) {
        final String cachedKey = dbs.hashCode() + "@" + ref.hashCode();
        return CC_ADB.pick(() -> new ADJ(ref, dbs), cachedKey);
    }

    static ADJ of(final Join join, final DBS dbs, final Kv<String, String> vectorPojo) {
        /*
         * 旧版升级修正，id 作为核心主键
         * - 如果没有指定 fromField，则使用 ID 作为关联字段
         * - 如果没有指定 toField，则使用 ID 作为关联字段
         */
        if (Objects.isNull(join.fromField()) || KName.KEY.equals(join.fromField())) {
            join.from(KName.ID);
        }
        if (Objects.isNull(join.toField()) || KName.KEY.equals(join.toField())) {
            join.to(KName.ID);
        }
        if (Objects.nonNull(vectorPojo.key())) {
            join.from(MMAdapt.of(vectorPojo.key()).vector());
        }
        if (Objects.nonNull(vectorPojo.value())) {
            join.to(MMAdapt.of(vectorPojo.value()).vector());
        }
        final String cachedKey = dbs.hashCode() + "@" + join.hashCode();
        return CC_ADB.pick(() -> new ADJ(join, dbs), cachedKey);
    }

    public ADJ alias(final Class<?> vertxDao, final String name, final String alias) {
        this.dbj.alias(vertxDao, name, alias);
        return this;
    }

    public ADJ alias(final Class<?> vertxDao, final Map<String, String> waitFor) {
        this.dbj.alias(vertxDao, waitFor);
        return this;
    }

    // 补齐原始的 DBJ 方法之后追加对应的 Async 方法

    // ========== 计数 ==========
    public Long count(final JsonObject criteriaJ) {
        return this.dbj.countJ(criteriaJ);
    }

    public Long count(final Map<String, Object> criteria) {
        return this.dbj.countJ(criteria);
    }

    public Long count(final String field, final Object value) {
        return this.dbj.countJ(field, value);
    }

    public Long count() {
        return this.dbj.countJ();
    }

    public Future<Long> countAsync(final JsonObject criteriaJ) {
        return this.dbj.countAsyncJ(criteriaJ);
    }

    public Future<Long> countAsync(final Map<String, Object> criteria) {
        return this.dbj.countAsyncJ(criteria);
    }

    public Future<Long> countAsync(final String field, final Object value) {
        return this.dbj.countAsyncJ(field, value);
    }

    public Future<Long> countAsync() {
        return this.dbj.countAsyncJ();
    }

    // ========== 分页 ==========

    // （可选别名：保留你之前的 search/searchAsync，如不需要可删除）
    public JsonObject search(final JsonObject queryJ) {
        return this.dbj.findPageJ(queryJ);
    }

    public Future<JsonObject> searchAsync(final JsonObject queryJ) {
        return this.dbj.findPageAsyncJ(queryJ);
    }

    // ========== 全量 ==========
    public JsonArray fetchAll() {
        return this.dbj.findAllJ();
    }

    public Future<JsonArray> fetchAllAsync() {
        return this.dbj.findAllAsyncJ();
    }

    // ========== 存在性(Exist) ==========
    public Boolean fetchExist(final JsonObject treeJ) {
        return this.dbj.findExistJ(treeJ);
    }

    public Future<Boolean> fetchExistAsync(final JsonObject treeJ) {
        return this.dbj.findExistAsyncJ(treeJ);
    }

    // ========== Full 查询（带联表/聚合等的完整查询）==========
    public JsonArray fetchFull(final JsonObject queryJ) {
        return this.dbj.findFullJ(queryJ);
    }

    public Future<JsonArray> fetchFullAsync(final JsonObject queryJ) {
        return this.dbj.findFullAsyncJ(queryJ);
    }

    // ========== 批量查询（Many）==========
    public JsonArray fetch(final JsonObject treeJ) {
        return this.dbj.findManyJ(treeJ);
    }

    public Future<JsonArray> fetchAsync(final JsonObject treeJ) {
        return this.dbj.findManyAsyncJ(treeJ);
    }

    public JsonArray fetch(final Map<String, Object> map) {
        return this.dbj.findManyJ(map);
    }

    public Future<JsonArray> fetchAsync(final Map<String, Object> map) {
        return this.dbj.findManyAsyncJ(map);
    }

    public JsonArray fetch(final String field, final Object value) {
        return this.dbj.findManyJ(field, value);
    }

    public Future<JsonArray> fetchAsync(final String field, final Object value) {
        return this.dbj.findManyAsyncJ(field, value);
    }

    public JsonArray fetch() {
        return this.dbj.findManyJ();
    }

    public Future<JsonArray> fetchAsync() {
        return this.dbj.findManyAsyncJ();
    }

    // ========== 按条件 MapJ（By）==========
    public JsonArray fetchBy(final JsonObject mapJ) {
        return this.dbj.findManyByJ(mapJ);
    }

    public Future<JsonArray> fetchByAsync(final JsonObject mapJ) {
        return this.dbj.findManyByAsyncJ(mapJ);
    }

    // ========== IN 查询（ManyIn）==========
    public JsonArray fetchIn(final String field, final List<?> values) {
        return this.dbj.findManyInJ(field, values);
    }

    public Future<JsonArray> fetchInAsync(final String field, final List<?> values) {
        return this.dbj.findManyInAsyncJ(field, values);
    }

    public JsonArray fetchIn(final String field, final Object... values) {
        return this.dbj.findManyInJ(field, values);
    }

    public Future<JsonArray> fetchInAsync(final String field, final Object... values) {
        return this.dbj.findManyInAsyncJ(field, values);
    }

    public JsonArray fetchIn(final String field, final JsonArray values) {
        return this.dbj.findManyInJ(field, values);
    }

    public Future<JsonArray> fetchInAsync(final String field, final JsonArray values) {
        return this.dbj.findManyInAsyncJ(field, values);
    }

    // ========== 单条查询（One）==========
    public JsonObject fetchOne(final JsonObject treeJ) {
        treeJ.put(VString.EMPTY, Boolean.TRUE);
        return this.dbj.findOneJ(treeJ);
    }

    public Future<JsonObject> fetchOneAsync(final JsonObject treeJ) {
        treeJ.put(VString.EMPTY, Boolean.TRUE);
        return this.dbj.findOneAsyncJ(treeJ);
    }

    public JsonObject fetchOne(final Map<String, Object> map) {
        return this.dbj.findOneJ(map);
    }

    public Future<JsonObject> fetchOneAsync(final Map<String, Object> map) {
        return this.dbj.findOneAsyncJ(map);
    }

    public JsonObject fetchOne(final String field, final Object value) {
        return this.dbj.findOneJ(field, value);
    }

    public Future<JsonObject> fetchOneAsync(final String field, final Object value) {
        return this.dbj.findOneAsyncJ(field, value);
    }

    public JsonObject fetchOne(final Serializable id) {
        return this.dbj.findOneJ(id);
    }

    public Future<JsonObject> fetchOneAsync(final Serializable id) {
        return this.dbj.findOneAsyncJ(id);
    }

    // ========== 单条查询（By）==========
    public JsonObject fetchOneBy(final JsonObject mapJ) {
        return this.dbj.findOneByJ(mapJ);
    }

    public Future<JsonObject> fetchOneByAsync(final JsonObject mapJ) {
        return this.dbj.findOneByAsyncJ(mapJ);
    }

    // ========== 删除（remove -> delete）==========
    public Boolean deleteBy(final JsonObject criteriaJ) {
        return this.dbj.removeByJ(criteriaJ);
    }

    public Future<Boolean> deleteByAsync(final JsonObject criteriaJ) {
        return this.dbj.removeByAsyncJ(criteriaJ);
    }

    public Boolean deleteBy(final Map<String, Object> criteria) {
        return this.dbj.removeByJ(criteria);
    }

    public Future<Boolean> deleteByAsync(final Map<String, Object> criteria) {
        return this.dbj.removeByAsyncJ(criteria);
    }

    public Boolean deleteBy(final String field, final Object value) {
        return this.dbj.removeByJ(field, value);
    }

    public Future<Boolean> deleteByAsync(final String field, final Object value) {
        return this.dbj.removeByAsyncJ(field, value);
    }

    public Boolean deleteBy(final Serializable id) {
        return this.dbj.removeByJ(id);
    }

    public Future<Boolean> deleteByAsync(final Serializable id) {
        return this.dbj.removeByAsyncJ(id);
    }

    // ========== 更新（update 保持不变）==========
    public JsonObject updateBy(final JsonObject criteriaJ, final JsonObject updateJ) {
        return this.dbj.updateByJ(criteriaJ, updateJ);
    }

    public Future<JsonObject> updateByAsync(final JsonObject criteriaJ, final JsonObject updateJ) {
        return this.dbj.updateByAsyncJ(criteriaJ, updateJ);
    }

    public JsonObject updateBy(final Map<String, Object> criteria, final JsonObject updateJ) {
        return this.dbj.updateByJ(criteria, updateJ);
    }

    public Future<JsonObject> updateByAsync(final Map<String, Object> criteria, final JsonObject updateJ) {
        return this.dbj.updateByAsyncJ(criteria, updateJ);
    }

    public JsonObject updateBy(final String field, final Object value, final JsonObject updateJ) {
        return this.dbj.updateByJ(field, value, updateJ);
    }

    public Future<JsonObject> updateByAsync(final String field, final Object value, final JsonObject updateJ) {
        return this.dbj.updateByAsyncJ(field, value, updateJ);
    }

    public JsonObject updateBy(final Serializable id, final JsonObject updateJ) {
        return this.dbj.updateByJ(id, updateJ);
    }

    public Future<JsonObject> updateByAsync(final Serializable id, final JsonObject updateJ) {
        return this.dbj.updateByAsyncJ(id, updateJ);
    }

    // ========== 插入（create -> insert）==========
    public JsonObject insert(final JsonObject insertJ) {
        return this.dbj.createJ(insertJ);
    }

    public Future<JsonObject> insertAsync(final JsonObject insertJ) {
        return this.dbj.createAsyncJ(insertJ);
    }
}
