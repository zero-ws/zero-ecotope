package io.zerows.epoch.database.jooq.operation;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.program.R2Mapping;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Pagination;
import io.r2mo.vertx.jooq.AsyncDBContext;
import io.r2mo.vertx.jooq.DBEx;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.cp.DataPool;
import io.zerows.epoch.database.jooq.JooqDsl;
import io.zerows.epoch.database.jooq.JooqInfix;
import io.zerows.epoch.database.jooq.util.JqAnalyzer;
import io.zerows.epoch.database.jooq.util.JqFlow;
import io.zerows.epoch.database.jooq.util.JqTool;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiPredicate;

public class ADB {

    private static final Cc<String, ADB> CC_JOOQ = Cc.openThread();

    // region 基本变量定义和构造函数
    private final DBEx<?> dbe;

    /**
     * 直接新版访问 {@link DBEx} 的入口，之后的内容不再访问
     */
    private <T> ADB(final Class<T> daoCls, final DBS dbs, final R2Vector vector) {
        this.dbe = DBEx.of(daoCls, dbs, vector);
    }

    // endregion

    // region 最终构造包域，所以此方法的访问会被内部访问

    /**
     * 工厂方法：基于给定 DAO 类、数据源以及映射文件创建/复用 {@link ADB} 实例。🧩
     *
     * <p>流程：先通过映射文件 {@code filename} 构造字段映射向量 {@link R2Vector}，
     * 再用 {@link AsyncDBContext#cached(Class, DBS, R2Vector)} 生成缓存键，
     * 最终由 {@link ADB#CC_JOOQ#pick(java.util.function.Supplier, String)} 复用或创建实例。</p>
     *
     * 新版引入 {@link MMAdapt} 构造 {@link R2Vector} 实现完整的数据交换映射信息，因此有了此处的 pojoFile 之后，流程
     * 如
     * <pre>
     *     绑定 {@link DBEx} 实例
     *        - {@link R2Vector}
     *           -> 可随时绑定也可换绑，在执行过程中也可随时更换
     *        - {@link DBS} 数据源实例
     *        - {@link Class} DAO 类
     *        - {@link Vertx} 引用
     * </pre>
     * 新版映射模型会直接采用新架构 {@link R2Vector} 来实现映射转换，它内置两个映射表
     * <pre>
     *     1. field ( Class ) -> field ( Json ), 数据类型 {@link R2Mapping}
     *     2. field ( Class ) -> column ( DB ), 数据类型 {@link R2Mapping}
     * </pre>
     *
     * @param daoCls   DAO 类（通常为 jOOQ 生成的 *Dao 类）
     * @param dbs      数据源描述对象 {@link DBS}（连接信息、方言等）
     * @param filename 映射文件名（用于解析并构建 {@link R2Vector} 字段映射）；可指向类路径或绝对路径
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB of(final Class<?> daoCls, final String filename, final DBS dbs) {
        Objects.requireNonNull(dbs, "[ ZERO ] 传入的数据源不可以为 null");
        final R2Vector vector;
        if (StrUtil.isNotBlank(filename)) {
            vector = MMAdapt.of(filename).vector();
        } else {
            vector = null;
        }
        final String cached = AsyncDBContext.cached(daoCls, dbs, vector);
        return CC_JOOQ.pick(() -> new ADB(daoCls, dbs, vector), cached);
    }
    // endregion

    @SuppressWarnings("all")
    private <R> DBEx<R> dbe() {
        return (DBEx<R>) this.dbe;
    }

    /*
     * 所有的方法都会有两个标记
     * map  ->  executed  --> map
     * - 支持用 ✅
     * - 不支持用 ❌
     * - 内部调用变体 🌸
     * 输入是 JsonObject 或 JsonArray 会支持
     * 输出是 JsonObject 或 JsonArray 会支持
     * 1）同一个方法的形态会有 8 种
     * 2）参考如下矩阵
     *                          同步方法             |          异步方法
     *    输入T / 输出T           xxxxx              |          xxxxxAsync
     *    输入J / 输出T           xxxxx              |          xxxxxAsync
     *    输入T / 输出J           xxxxxJ             |          xxxxxJAsync
     *    输入J / 输出J           xxxxxJ             |          xxxxxJAsync
     * 3）带上单记录操作和批量操作会从 8 种变成 16 种
     */
    // region 查找（所有） x 4
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchAll() {
        return this.<T>dbe().findAll();
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public JsonArray fetchJAll() {
        return this.dbe().findAllJ();
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAllAsync() {
        return this.<T>dbe().findAllAsync();
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public Future<JsonArray> fetchJAllAsync() {
        return this.dbe().findAllJAsync();
    }
    // endregion

    // region 计数（所有） x 2
    // map ❌ ------> executed ✅ ------> map ❌
    public Long countAll() {
        return this.dbe().count().orElse(0L);
    }


    // map ❌ ------> executed ✅ ------> map ❌
    public Future<Long> countAllAsync() {
        return this.dbe().countAsync();
    }
    // endregion

    // region 搜索方法 x 4

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<Pagination<T>> searchAsync(final JsonObject query) {
        return this.<T>dbe().findPageAsync(query);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> searchJAsync(final JsonObject query) {
        return this.<T>dbe().findPageAsyncJ(query);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Pagination<T> search(final JsonObject query) {
        return this.<T>dbe().findPage(query);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject searchJ(final JsonObject query) {
        return this.<T>dbe().findPageJ(query);
    }

    // endregion

    // region 单字段 IN 多记录查询 x 12
    //  map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchInAsync(final String field, final Object... values) {
        return this.<T>dbe().findManyInAsync(field, values);
    }

    //  map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchInAsync(final String field, final JsonArray values) {
        return this.<T>fetchInAsync(field, values.getList());
    }

    //  map ❌ ------> executed ✅ ------> map ❌
    public <T, K> Future<List<T>> fetchInAsync(final String field, final Collection<K> collection) {
        return this.<T>dbe().findManyInAsync(field, new ArrayList<>(collection));
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> fetchJInAsync(final String field, final Object... values) {
        return this.<T>dbe().findManyInAsyncJ(field, values);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> fetchJInAsync(final String field, final JsonArray values) {
        return this.<T>dbe().findManyInAsyncJ(field, values.getList());
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T, K> Future<JsonArray> fetchJInAsync(final String field, final Collection<K> collection) {
        return this.<T>dbe().findManyInAsyncJ(field, collection);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchIn(final String field, final Object... values) {
        return this.<T>dbe().findManyIn(field, values);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchIn(final String field, final JsonArray values) {
        return this.<T>dbe().findManyIn(field, values.getList());
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T, K> List<T> fetchIn(final String field, final Collection<K> collection) {
        return this.<T>dbe().findManyIn(field, new ArrayList<>(collection));
    }


    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJIn(final String field, final Object... values) {
        return this.<T>dbe().findManyInJ(field, values);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJIn(final String field, final JsonArray values) {
        return this.<T>dbe().findManyInJ(field, values.getList());
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T, K> JsonArray fetchJIn(final String field, final Collection<K> collection) {
        return this.<T>dbe().findManyInJ(field, new ArrayList<>(collection));
    }
    // endregion

    //  region 单字段 = 多记录查询 x 4
    //  map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAsync(final String field, final Object value) {
        return this.<T>dbe().findManyAsync(field, value);
    }

    //  map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> fetchJAsync(final String field, final Object value) {
        return this.<T>dbe().findManyAsyncJ(field, value);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> fetch(final String field, final Object value) {
        return this.<T>dbe().findMany(field, value);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJ(final String field, final Object value) {
        return this.<T>dbe().findManyJ(field, value);
    }
    // endregion

    // region 查询条件树 = 多记录查询 x 24, 带有 AND / OR 变种
    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAsync(final JsonObject criteria) {
        return this.<T>dbe().findManyAsync(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> fetchJAsync(final JsonObject criteria) {
        return this.<T>dbe().findManyAsyncJ(criteria);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAndAsync(final JsonObject criteria) {
        return this.fetchAsync(criteria.put(VString.EMPTY, Boolean.TRUE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchOrAsync(final JsonObject criteria) {
        return this.fetchAsync(criteria.put(VString.EMPTY, Boolean.FALSE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public Future<JsonArray> fetchJAndAsync(final JsonObject criteria) {
        return this.fetchJAsync(criteria.put(VString.EMPTY, Boolean.TRUE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public Future<JsonArray> fetchJOrAsync(final JsonObject criteria) {
        return this.fetchJAsync(criteria.put(VString.EMPTY, Boolean.FALSE));
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAsync(final JsonObject criteria, final QSorter sorter) {
        return this.<T>dbe().findManyAsync(criteria, sorter);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> fetchJAsync(final JsonObject criteria, final QSorter sorter) {
        return this.<T>dbe().findManyAsyncJ(criteria, sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchAndAsync(final JsonObject criteria, final QSorter sorter) {
        return this.fetchAsync(criteria.put(VString.EMPTY, Boolean.TRUE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> fetchOrAsync(final JsonObject criteria, final QSorter sorter) {
        return this.fetchAsync(criteria.put(VString.EMPTY, Boolean.FALSE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public Future<JsonArray> fetchJAndAsync(final JsonObject criteria, final QSorter sorter) {
        return this.fetchJAsync(criteria.put(VString.EMPTY, Boolean.TRUE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public Future<JsonArray> fetchJOrAsync(final JsonObject criteria, final QSorter sorter) {
        return this.fetchJAsync(criteria.put(VString.EMPTY, Boolean.FALSE), sorter);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetch(final JsonObject criteria) {
        return this.<T>dbe().findMany(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJ(final JsonObject criteria) {
        return this.<T>dbe().findManyJ(criteria);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchAnd(final JsonObject criteria) {
        return this.fetch(criteria.put(VString.EMPTY, Boolean.TRUE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchOr(final JsonObject criteria) {
        return this.fetch(criteria.put(VString.EMPTY, Boolean.FALSE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJAnd(final JsonObject criteria) {
        return this.<T>fetchJ(criteria.put(VString.EMPTY, Boolean.TRUE));
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJOr(final JsonObject criteria) {
        return this.<T>fetchJ(criteria.put(VString.EMPTY, Boolean.FALSE));
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetch(final JsonObject criteria, final QSorter sorter) {
        return this.<T>dbe().findMany(criteria, sorter);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJ(final JsonObject criteria, final QSorter sorter) {
        return this.<T>dbe().findManyJ(criteria, sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchAnd(final JsonObject criteria, final QSorter sorter) {
        return this.fetch(criteria.put(VString.EMPTY, Boolean.TRUE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> fetchOr(final JsonObject criteria, final QSorter sorter) {
        return this.fetch(criteria.put(VString.EMPTY, Boolean.FALSE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJAnd(final JsonObject criteria, final QSorter sorter) {
        return this.<T>fetchJ(criteria.put(VString.EMPTY, Boolean.TRUE), sorter);
    }

    // ( 🌸 ) map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray fetchJOr(final JsonObject criteria, final QSorter sorter) {
        return this.<T>fetchJ(criteria.put(VString.EMPTY, Boolean.FALSE), sorter);
    }
    // endregion

    // region 按照ID查询 x 4
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> fetchByIdAsync(final Object id) {
        return this.<T>dbe().findOneAsync((Serializable) id);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> fetchJByIdAsync(final Object id) {
        return this.<T>dbe().findOneAsyncJ((Serializable) id);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T fetchById(final Object id) {
        return this.<T>dbe().findOne((Serializable) id).orElse(null);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject fetchJById(final Object id) {
        return this.<T>dbe().findOneJ((Serializable) id);
    }
    // endregion

    // region 单字段 = 单记录查询 x 4
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> fetchOneAsync(final String field, final Object value) {
        return this.<T>dbe().findOneAsync(field, value);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> fetchJOneAsync(final String field, final Object value) {
        return this.<T>dbe().findOneAsyncJ(field, value);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T fetchOne(final String field, final Object value) {
        return this.<T>dbe().findOne(field, value).orElse(null);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject fetchJOne(final String field, final Object value) {
        return this.<T>dbe().findOneJ(field, value);
    }
    // endregion

    // region 查询条件数 = 单记录查询 x 4，强制 AND 变种
    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> fetchOneAsync(final JsonObject criteria) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().findOneAsync(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> fetchJOneAsync(final JsonObject criteria) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().findOneAsyncJ(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T fetchOne(final JsonObject criteria) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().findOne(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject fetchJOne(final JsonObject criteria) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().findOneJ(criteria);
    }
    // endregion

    // region （write）插入单条数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> insertAsync(final T entity) {
        return this.<T>dbe().createAsync(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> insertAsync(final JsonObject data) {
        return this.<T>dbe().createAsync(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> insertJAsync(final T entity) {
        return this.<T>dbe().createAsyncJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> insertJAsync(final JsonObject data) {
        return this.<T>dbe().createAsyncJ(data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T insert(final T entity) {
        return this.<T>dbe().create(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T insert(final JsonObject data) {
        return this.<T>dbe().create(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject insertJ(final T entity) {
        return this.<T>dbe().createJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject insertJ(final JsonObject data) {
        return this.<T>dbe().createJ(data);
    }
    // endregion

    // region （write）插入批量数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> insertAsync(final List<T> entities) {
        return this.<T>dbe().createAsync(entities);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> insertAsync(final JsonArray input) {
        return this.<T>dbe().createAsync(input);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> insertJAsync(final List<T> list) {
        return this.<T>dbe().createAsyncJ(list);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> insertJAsync(final JsonArray input) {
        return this.<T>dbe().createAsyncJ(input);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> insert(final List<T> entities) {
        return this.<T>dbe().create(entities);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> insert(final JsonArray data) {
        return this.<T>dbe().create(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray insertJ(final List<T> list) {
        return this.<T>dbe().createJ(list);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray insertJ(final JsonArray data) {
        return this.<T>dbe().createJ(data);
    }

    // endregion

    // region（write）更新单条数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T update(final T entity) {
        return this.<T>dbe().update(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T update(final JsonObject data) {
        return this.<T>dbe().update(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final T entity) {
        return this.<T>dbe().updateJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final JsonObject data) {
        return this.<T>dbe().updateJ(data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> updateAsync(final T entity) {
        return this.<T>dbe().updateAsync(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> updateAsync(final JsonObject data) {
        return this.<T>dbe().updateAsync(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> updateAsyncJ(final T entity) {
        return this.<T>dbe().updateAsyncJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> updateAsyncJ(final JsonObject data) {
        return this.<T>dbe().updateAsyncJ(data);
    }
    // endregion

    // region（write）更新批量数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> update(final List<T> entities) {
        return this.<T>dbe().update(entities);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> update(final JsonArray data) {
        return this.<T>dbe().update(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray updateJ(final List<T> entities) {
        return this.<T>dbe().updateJ(entities);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray updateJ(final JsonArray data) {
        return this.<T>dbe().updateJ(data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> updateAsync(final List<T> entities) {
        return this.<T>dbe().updateAsync(entities);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> updateAsync(final JsonArray data) {
        return this.<T>dbe().updateAsync(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> updateAsyncJ(final List<T> entities) {
        return this.<T>dbe().updateAsyncJ(entities);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> updateAsyncJ(final JsonArray input) {
        return this.<T>dbe().updateAsyncJ(input);
    }

    // endregion

    // region（write）按照ID更新单条数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T update(final Object id, final T updated) {
        return this.<T>dbe().updateBy((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T update(final Object id, final JsonObject data) {
        return this.<T>dbe().updateBy((Serializable) id, data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final Object id, final T updated) {
        return this.dbe().updateByJ((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final Object id, final JsonObject data) {
        return this.dbe().updateByJ((Serializable) id, data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T, ID extends Serializable> Future<T> updateAsync(final ID id, final T updated) {
        return this.<T>dbe().updateByAsync(id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T, ID extends Serializable> Future<T> updateAsync(final ID id, final JsonObject data) {
        return this.<T>dbe().updateByAsync(id, data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T, ID extends Serializable> Future<JsonObject> updateJAsync(final ID id, final T updated) {
        return this.<T>dbe().updateByAsyncJ(id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T, ID extends Serializable> Future<JsonObject> updateJAsync(final ID id, final JsonObject data) {
        return this.<T>dbe().updateByAsyncJ(id, data);
    }
    // endregion

    // region（write）按照“条件”更新单条数据 x 8
    public <T> T update(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateBy(criteria, updated);
    }

    public <T> T update(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateBy(criteria, data);
    }

    public <T> JsonObject updateJ(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateByJ(criteria, updated);
    }

    public <T> JsonObject updateJ(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateByJ(criteria, data);
    }

    public <T> Future<T> updateAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsync(criteria, updated);
    }

    public <T> Future<T> updateAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsync(criteria, data);
    }

    public <T> Future<JsonObject> updateJAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsyncJ(criteria, updated);
    }

    public <T> Future<JsonObject> updateJAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsyncJ(criteria, data);
    }
    // endregion

    // -------------------- Pojo File --------------------
    // region 旧版 Jooq 操作，逐步迁移中
    @Deprecated
    public static ADB of(final Class<?> clazz, final DataPool pool) {
        final JooqDsl dsl = JooqInfix.getDao(clazz, pool);
        return CC_JOOQ.pick(() -> new ADB(clazz, dsl), dsl.poolKey());
    }

    /* Analyzer */
    private transient JqAnalyzer analyzer;
    /* Aggre */
    private transient JqAggregator aggregator;
    /* Writer */
    private transient JqWriter writer;
    /* Reader */
    private transient JqReader reader;
    /*
     * New Structure for usage
     */
    private transient JqFlow workflow;

    @Deprecated
    protected <T> ADB(final Class<T> clazz, final JooqDsl dsl) {
        this.dbe = null;
        // this.mapped = null;
        /* New exception to avoid programming missing */
        // this.daoCls = clazz;

        /* Analyzing column for Jooq */
        this.analyzer = JqAnalyzer.create(dsl);
        this.aggregator = JqAggregator.create(this.analyzer);

        /* Reader connect Analayzer */
        this.reader = JqReader.create(this.analyzer);

        /* Writer connect Reader */
        this.writer = JqWriter.create(this.analyzer);

        /* New Structure */
        this.workflow = JqFlow.create(this.analyzer);
    }

    @Deprecated
    public JqAnalyzer analyzer() {
        return this.analyzer;
    }

    @Deprecated
    public Set<String> columns() {
        return this.analyzer.columns().keySet();
    }

    @Deprecated
    public String table() {
        return this.analyzer.table().getName();
    }

    @Deprecated
    private JsonObject andOr(final JsonObject criteria) {
        if (!criteria.containsKey(VString.EMPTY)) {
            criteria.put(VString.EMPTY, Boolean.TRUE);
        }
        return criteria;
    }

    // endregion

    // -------------------- Upsert Operation ( INSERT / UPDATE ) ---------

    /*
     * upsert(id, Tool)
     *      <-- upsert(id, JsonObject)
     *      <-- upsert(id, JsonObject, pojo)
     *      <-- upsertJ(id, Tool)
     *      <-- upsertJ(id, JsonObject)
     *      <-- upsertJ(id, JsonObject, pojo)
     */
    public <T> T upsert(final Object id, final T updated) {
        return this.writer.upsert(id, updated);
    }

    public <T> T upsert(final Object id, final JsonObject data) {
        return this.upsert(id, (T) this.workflow.input(data));
    }

    public <T> T upsert(final Object id, final JsonObject data, final String pojo) {
        return this.upsert(id, (T) JqFlow.create(this.analyzer, pojo).input(data));
    }

    public <T> JsonObject upsertJ(final Object id, final T updated) {
        return this.workflow.output(this.upsert(id, updated));
    }

    public <T> JsonObject upsertJ(final Object id, final JsonObject data) {
        return this.workflow.output(this.upsert(id, (T) this.workflow.input(data)));
    }

    public <T> JsonObject upsertJ(final Object id, final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.output(this.upsert(id, (T) flow.input(data)));
    }

    /*
     * upsertAsync(id, Tool)
     *      <-- upsertAsync(id, JsonObject)
     *      <-- upsertAsync(id, JsonObject, pojo)
     *      <-- upsertJAsync(id, Tool)
     *      <-- upsertJAsync(id, JsonObject)
     *      <-- upsertJAsync(id, JsonObject, pojo)
     */
    public <T> Future<T> upsertAsync(final Object id, final T updated) {
        return this.writer.upsertAsync(id, updated);
    }

    public <T> Future<T> upsertAsync(final Object id, final JsonObject data) {
        return this.workflow.<T>inputAsync(data).compose(updated -> this.upsertAsync(id, updated));
    }

    public <T> Future<T> upsertAsync(final Object id, final JsonObject data, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).<T>inputAsync(data).compose(updated -> this.upsertAsync(id, updated));
    }

    public <T> Future<JsonObject> upsertJAsync(final Object id, final T updated) {
        return this.upsertAsync(id, updated).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonObject> upsertJAsync(final Object id, final JsonObject data) {
        return this.workflow.<T>inputAsync(data).compose(updated -> this.upsertAsync(id, updated)).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonObject> upsertJAsync(final Object id, final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.<T>inputAsync(data).compose(updated -> this.upsertAsync(id, updated)).compose(flow::outputAsync);
    }

    /*
     * upsert(criteria, Tool)
     *      <-- upsert(criteria, JsonObject)
     *      <-- upsertJ(criteria, Tool)
     *      <-- upsertJ(criteria, JsonObject)
     * upsert(criteria, Tool, pojo)
     *      <-- upsert(criteria, JsonObject, pojo)
     *      <-- upsertJ(criteria, Tool, pojo)
     *      <-- upsertJ(criteria, JsonObject, pojo)
     */
    public <T> T upsert(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.writer.upsert(this.workflow.inputQrJ(criteria), updated);
    }

    public <T> T upsert(final JsonObject criteria, final JsonObject data) {
        return this.upsert(criteria, (T) this.workflow.input(data));
    }

    public <T> JsonObject upsertJ(final JsonObject criteria, final T updated) {
        return this.workflow.output(this.upsert(criteria, updated));
    }

    public <T> JsonObject upsertJ(final JsonObject criteria, final JsonObject data) {
        return this.workflow.output(this.upsert(criteria, (T) this.workflow.input(data)));
    }

    public <T> T upsert(final JsonObject criteria, final T updated, final String pojo) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.writer.upsert(JqFlow.create(this.analyzer, pojo).inputQrJ(criteria), updated);
    }

    public <T> T upsert(final JsonObject criteria, final JsonObject data, final String pojo) {
        return this.upsert(criteria, (T) JqFlow.create(this.analyzer, pojo).input(data), pojo);
    }

    public <T> JsonObject upsertJ(final JsonObject criteria, final T updated, final String pojo) {
        return this.workflow.output(this.upsert(criteria, updated, pojo));
    }

    public <T> JsonObject upsertJ(final JsonObject criteria, final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.output(this.upsert(criteria, (T) flow.input(data), pojo));
    }

    /*
     * upsertAsync(criteria, Tool)
     *      <-- upsertAsync(criteria, JsonObject)
     *      <-- upsertJAsync(criteria, Tool)
     *      <-- upsertJAsync(criteria, JsonObject)
     * upsertAsync(criteria, Tool, pojo)
     *      <-- upsertAsync(criteria, JsonObject, pojo)
     *      <-- upsertJAsync(criteria, Tool, pojo)
     *      <-- upsertJAsync(criteria, JsonObject, pojo)
     */
    public <T> Future<T> upsertAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.workflow.inputQrJAsync(criteria).compose(normalized -> this.writer.upsertAsync(normalized, updated));
    }

    public <T> Future<T> upsertAsync(final JsonObject criteria, final JsonObject data) {
        return JqTool.joinAsync(criteria, data, this.workflow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (T) response.resultAt(VValue.ONE)));
    }

    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final T updated) {
        return this.upsertAsync(criteria, updated).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final JsonObject data) {
        return JqTool.joinAsync(criteria, data, this.workflow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (T) response.resultAt(VValue.ONE)))
            .compose(this.workflow::outputAsync);
    }

    public <T> Future<T> upsertAsync(final JsonObject criteria, final T updated, final String pojo) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria).compose(normalized -> this.writer.upsertAsync(normalized, updated));
    }

    public <T> Future<T> upsertAsync(final JsonObject criteria, final JsonObject data, final String pojo) {
        return JqTool.joinAsync(criteria, data, JqFlow.create(this.analyzer, pojo))
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (T) response.resultAt(VValue.ONE), pojo));
    }

    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final T updated, final String pojo) {
        return this.upsertAsync(criteria, updated, pojo).compose(JqFlow.create(this.analyzer, pojo)::outputAsync);
    }

    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return JqTool.joinAsync(criteria, data, flow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (T) response.resultAt(VValue.ONE), pojo))
            .compose(flow::outputAsync);
    }

    /*
     * upsert(criteria, list, finder)
     *      <-- upsert(criteria, JsonArray, finder)
     *      <-- upsertJ(criteria, list, finder)
     *      <-- upsertJ(criteria, JsonArray, finder)
     * upsert(criteria, list, finder, pojo)
     *      <-- upsert(criteria, JsonArray, finder, pojo)
     *      <-- upsertJ(criteria, list, finder, pojo)
     *      <-- upsertJ(criteria, JsonArray, finder, pojo)
     */
    public <T> List<T> upsert(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder) {
        return this.writer.upsert(this.workflow.inputQrJ(criteria), list, finder);
    }

    public <T> List<T> upsert(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder) {
        return this.upsert(criteria, this.workflow.input(data), finder);
    }

    public <T> JsonArray upsertJ(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder) {
        return this.workflow.output(this.upsert(criteria, list, finder));
    }

    public <T> JsonArray upsertJ(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder) {
        return this.workflow.output(this.upsert(criteria, this.workflow.input(data), finder));
    }

    public <T> List<T> upsert(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder, final String pojo) {
        return this.writer.upsert(JqFlow.create(this.analyzer, pojo).inputQrJ(criteria), list, finder);
    }

    public <T> List<T> upsert(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder, final String pojo) {
        return this.upsert(criteria, JqFlow.create(this.analyzer, pojo).input(data), finder, pojo);
    }

    public <T> JsonArray upsertJ(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).output(this.upsert(criteria, list, finder, pojo));
    }

    public <T> JsonArray upsertJ(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.output(this.upsert(criteria, flow.input(data), finder, pojo));
    }

    /*
     * upsertAsync(criteria, list, finder)
     *      <-- upsertAsync(criteria, JsonArray, finder)
     *      <-- upsertJAsync(criteria, list, finder)
     *      <-- upsertJAsync(criteria, JsonArray, finder)
     * upsertAsync(criteria, list, finder, pojo)
     *      <-- upsertAsync(criteria, JsonArray, finder, pojo)
     *      <-- upsertJAsync(criteria, list, finder, pojo)
     *      <-- upsertJAsync(criteria, JsonArray, finder, pojo)
     */
    public <T> Future<List<T>> upsertAsync(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder) {
        return this.workflow.inputQrJAsync(criteria).compose(normalized -> this.writer.upsertAsync(normalized, list, finder));
    }

    public <T> Future<List<T>> upsertAsync(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder) {
        return JqTool.joinAsync(criteria, data, this.workflow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (List<T>) response.resultAt(VValue.ONE), finder));
    }

    public <T> Future<JsonArray> upsertJAsync(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder) {
        return this.upsertAsync(criteria, list, finder).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonArray> upsertJAsync(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder) {
        return JqTool.joinAsync(criteria, data, this.workflow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (List<T>) response.resultAt(VValue.ONE), finder))
            .compose(this.workflow::outputAsync);
    }

    public <T> Future<List<T>> upsertAsync(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria).compose(normalized -> this.writer.upsertAsync(normalized, list, finder));
    }

    public <T> Future<List<T>> upsertAsync(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder, final String pojo) {
        return JqTool.joinAsync(criteria, data, JqFlow.create(this.analyzer, pojo))
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (List<T>) response.resultAt(VValue.ONE), finder, pojo));
    }

    public <T> Future<JsonArray> upsertJAsync(final JsonObject criteria, final List<T> list, final BiPredicate<T, T> finder, final String pojo) {
        return this.upsertAsync(criteria, list, finder, pojo).compose(JqFlow.create(this.analyzer, pojo)::outputAsync);
    }

    public <T> Future<JsonArray> upsertJAsync(final JsonObject criteria, final JsonArray data, final BiPredicate<T, T> finder, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return JqTool.joinAsync(criteria, data, flow)
            .compose(response -> this.upsertAsync(response.resultAt(VValue.IDX), (List<T>) response.resultAt(VValue.ONE), finder, pojo))
            .compose(flow::outputAsync);
    }

    // -------------------- DELETE --------------------
    /*
     * delete(Tool)
     *      <-- delete(JsonObject)
     *      <-- delete(JsonObject, pojo)
     *      <-- deleteJ(Tool)
     *      <-- deleteJ(JsonObject)
     *      <-- deleteJ(JsonObject, pojo)
     */
    public <T> T delete(final T entity) {
        return this.writer.delete(entity);
    }

    public <T> T delete(final JsonObject data) {
        return this.delete((T) this.workflow.input(data));
    }

    public <T> T delete(final JsonObject data, final String pojo) {
        return this.delete((T) JqFlow.create(this.analyzer, pojo).input(data));
    }

    public <T> JsonObject deleteJ(final T entity) {
        return this.workflow.output(this.delete(entity));
    }

    public <T> JsonObject deleteJ(final JsonObject data) {
        return this.workflow.output(this.delete((T) this.workflow.input(data)));
    }

    public <T> JsonObject deleteJ(final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.output(this.delete((T) flow.input(data)));
    }

    /*
     * deleteAsync(Tool)
     *      <-- deleteAsync(JsonObject)
     *      <-- deleteAsync(JsonObject, pojo)
     *      <-- deleteJAsync(Tool)
     *      <-- deleteJAsync(JsonObject)
     *      <-- deleteJAsync(JsonObject, pojo)
     */
    public <T, ID> Future<T> deleteAsync(final T entity) {
        return this.writer.deleteAsync(entity);
    }

    public <T, ID> Future<T> deleteAsync(final JsonObject data) {
        return this.workflow.<T>inputAsync(data).compose(this::deleteAsync);
    }

    public <T, ID> Future<T> deleteAsync(final JsonObject data, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).<T>inputAsync(data).compose(this::deleteAsync);
    }

    public <T, ID> Future<JsonObject> deleteJAsync(final T entity) {
        return this.deleteAsync(entity).compose(this.workflow::outputAsync);
    }

    public <T, ID> Future<JsonObject> deleteJAsync(final JsonObject data) {
        return this.workflow.<T>inputAsync(data).compose(this::deleteAsync).compose(this.workflow::outputAsync);
    }

    public <T, ID> Future<JsonObject> deleteJAsync(final JsonObject data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.<T>inputAsync(data).compose(this::deleteAsync).compose(flow::outputAsync);
    }


    /*
     * delete(List<Tool>)
     *      <-- delete(JsonArray)
     *      <-- delete(JsonArray, pojo)
     *      <-- deleteJ(List<Tool>)
     *      <-- deleteJ(JsonArray)
     *      <-- deleteJ(JsonArray, pojo)
     */
    public <T> List<T> delete(final List<T> entity) {
        return this.writer.delete(entity);
    }

    public <T> List<T> delete(final JsonArray data) {
        return this.delete(this.workflow.input(data));
    }

    public <T> List<T> delete(final JsonArray data, final String pojo) {
        return this.delete(JqFlow.create(this.analyzer, pojo).input(data));
    }

    public <T> JsonArray deleteJ(final List<T> entity) {
        return this.workflow.output(this.delete(entity));
    }

    public <T> JsonArray deleteJ(final JsonArray data) {
        return this.workflow.output(this.delete((List<T>) this.workflow.input(data)));
    }

    public <T> JsonArray deleteJ(final JsonArray data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.output(this.delete((List<T>) flow.input(data)));
    }

    /*
     * deleteAsync(List<Tool>)
     *      <-- deleteAsync(JsonArray)
     *      <-- deleteAsync(JsonArray, pojo)
     *      <-- deleteJAsync(List<Tool>)
     *      <-- deleteJAsync(JsonArray)
     *      <-- deleteJAsync(JsonArray, pojo)
     */
    public <T> Future<List<T>> deleteAsync(final List<T> entity) {
        return this.writer.deleteAsync(entity);
    }

    public <T> Future<List<T>> deleteAsync(final JsonArray data) {
        return this.workflow.<T>inputAsync(data).compose(this::deleteAsync);
    }

    public <T> Future<List<T>> deleteAsync(final JsonArray data, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).<T>inputAsync(data).compose(this::deleteAsync);
    }

    public <T> Future<JsonArray> deleteJAsync(final List<T> entity) {
        return this.deleteAsync(entity).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonArray> deleteJAsync(final JsonArray data) {
        return this.workflow.<T>inputAsync(data).compose(this::deleteAsync).compose(this.workflow::outputAsync);
    }

    public <T> Future<JsonArray> deleteJAsync(final JsonArray data, final String pojo) {
        final JqFlow flow = JqFlow.create(this.analyzer, pojo);
        return flow.<T>inputAsync(data).compose(this::deleteAsync).compose(flow::outputAsync);
    }


    /*
     * deleteById(id)
     * deleteByIds(Collection<ID> ids)
     * deleteByIdAsync(id)
     * deleteByIdAsyncs(Collection<ID> ids)
     */
    public final Boolean deleteById(final Object id) {
        return this.writer.deleteById(Arrays.asList(id));
    }

    public Boolean deleteByIds(final Collection<Object> ids) {
        return this.writer.deleteById(ids);
    }

    public final Future<Boolean> deleteByIdAsync(final Object id) {
        return this.writer.deleteByIdAsync(Arrays.asList(id));
    }

    public Future<Boolean> deleteByIdsAsync(final Collection<Object> ids) {
        return this.writer.deleteByIdAsync(ids);
    }

    /*
     * deleteBy(JsonObject)
     * deleteBy(JsonObject, pojo)
     * deleteByAsync(JsonObject)
     * deleteByAsync(JsonObject, pojo)
     */
    /* (Async / Sync) Delete by Filters */
    public Future<Boolean> deleteByAsync(final JsonObject criteria) {                                                 // Unique Forced
        return this.workflow.inputQrJAsync(this.andOr(criteria)).compose(this.writer::deleteByAsync);
    }

    public Future<Boolean> deleteByAsync(final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(this.andOr(criteria)).compose(this.writer::deleteByAsync);
    }

    public Boolean deleteBy(final JsonObject criteria) {                                          // Unique Forced
        return this.writer.deleteBy(this.workflow.inputQrJ(this.andOr(criteria)));
    }

    public Boolean deleteBy(final JsonObject criteria, final String pojo) {
        return this.writer.deleteBy(JqFlow.create(this.analyzer, pojo).inputQrJ(this.andOr(criteria)));
    }

    // -------------------- Exist Operation --------------------
    /*
     * existById(key)
     *      <-- missById(key)
     * existByIdAsync(key)
     *      <-- missByIdAsync(key)
     */

    public Boolean existById(final Object id) {
        return this.reader.existById(id);
    }

    public Future<Boolean> existByIdAsync(final Object id) {
        return this.reader.existByIdAsync(id);
    }

    public Boolean missById(final Object id) {
        return !this.existById(id);
    }

    public Future<Boolean> missByIdAsync(final Object id) {
        return this.existByIdAsync(id)
            .compose(result -> Future.succeededFuture(!result));
    }

    /*
     * exist(JsonObject)
     *      <-- miss(JsonObject)
     * exist(JsonObject, pojo)
     *      <-- miss(JsonObject, pojo)
     * existAsync(JsonObject)
     *      <-- missAsync(JsonObject)
     * existAsync(JsonObject, pojo)
     *      <-- missAsync(JsonObject, pojo)
     */

    public Boolean exist(final JsonObject criteria) {
        return this.reader.exist(this.workflow.inputQrJ(criteria));
    }

    public Boolean exist(final JsonObject criteria, final String pojo) {
        return this.reader.exist(JqFlow.create(this.analyzer, pojo).inputQrJ(criteria));
    }

    public Future<Boolean> existAsync(final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria).compose(this.reader::existAsync);
    }

    public Future<Boolean> existAsync(final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria).compose(this.reader::existAsync);
    }

    public Boolean miss(final JsonObject criteria) {
        return !this.exist(criteria);
    }

    public Boolean miss(final JsonObject criteria, final String pojo) {
        return !this.exist(criteria, pojo);
    }

    public Future<Boolean> missAsync(final JsonObject criteria) {
        return this.existAsync(criteria).compose(existing -> Future.succeededFuture(!existing));
    }

    public Future<Boolean> missAsync(final JsonObject criteria, final String pojo) {
        return this.existAsync(criteria, pojo).compose(existing -> Future.succeededFuture(!existing));
    }

    // -------------------- Group Operation ------------
    /*
     * group(String)
     *      <-- groupJ(String)
     *      <-- groupAsync(String)
     *      <-- groupJAsync(String)
     * group(JsonObject, String)
     *      <-- groupAsync(JsonObject, String)
     *      <-- groupJ(JsonObject, String)
     *      <-- groupJAsync(JsonObject, String)
     */
    public <T> ConcurrentMap<String, List<T>> group(final String field) {
        return this.aggregator.group(field);
    }

    public <T> ConcurrentMap<String, JsonArray> groupJ(final String field) {
        return this.workflow.output(this.group(field));
    }

    public <T> Future<ConcurrentMap<String, List<T>>> groupAsync(final String field) {
        return Future.succeededFuture(this.group(field));  // Async Future
    }

    public <T> Future<ConcurrentMap<String, JsonArray>> groupJAsync(final String field) {
        return Future.succeededFuture(this.group(field)).compose(this.workflow::outputAsync);
    }

    public <T> ConcurrentMap<String, List<T>> group(final JsonObject criteria, final String field) {
        return this.aggregator.group(this.workflow.inputQrJ(criteria), field);
    }

    public <T> ConcurrentMap<String, JsonArray> groupJ(final JsonObject criteria, final String field) {
        return this.workflow.output(this.group(criteria, field));
    }

    public <T> Future<ConcurrentMap<String, List<T>>> groupAsync(final JsonObject criteria, final String field) {
        return Future.succeededFuture(this.group(criteria, field));  // Async Future
    }

    public <T> Future<ConcurrentMap<String, JsonArray>> groupJAsync(final JsonObject criteria, final String field) {
        return Future.succeededFuture(this.group(criteria, field)).compose(this.workflow::outputAsync); // Async Future
    }

    // -------------------- Count Operation ------------
    /*
     * count(JsonObject)
     * count(JsonObject, pojo)
     * countAsync(JsonObject)
     * countAsync(JsonObject, pojo)
     */

    public Long count(final JsonObject criteria) {
        return this.aggregator.count(this.workflow.inputQrJ(criteria));
    }

    public Future<Long> countAsync(final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria).compose(this.aggregator::countAsync);
    }


    /*
     * countBy(JsonObject, String)
     *      <-- countByAsync(JsonObject, String)
     *      <-- countBy(String)
     *      <-- countByAsync(String)
     * countBy(JsonObject, String...)
     *      <-- countByAsync(JsonObject, String...)
     *      <-- countBy(String...)
     *      <-- countByAsync(String...)
     */
    public ConcurrentMap<String, Integer> countBy(final JsonObject criteria, final String groupField) {
        return this.aggregator.countBy(this.workflow.inputQrJ(criteria), groupField);
    }

    public ConcurrentMap<String, Integer> countBy(final String groupField) {
        return this.countBy(new JsonObject(), groupField);
    }

    public Future<ConcurrentMap<String, Integer>> countByAsync(final JsonObject criteria, final String groupField) {
        return Future.succeededFuture(this.countBy(criteria, groupField));
    }

    public Future<ConcurrentMap<String, Integer>> countByAsync(final String groupField) {
        return Future.succeededFuture(this.countBy(new JsonObject(), groupField));
    }

    public JsonArray countBy(final JsonObject criteria, final String... groupFields) {
        return this.aggregator.countBy(this.workflow.inputQrJ(criteria), groupFields);
    }

    public JsonArray countBy(final String... groupFields) {
        return this.countBy(new JsonObject(), groupFields);
    }

    public Future<JsonArray> countByAsync(final String... groupFields) {
        return Future.succeededFuture(this.countBy(new JsonObject(), groupFields));
    }

    public Future<JsonArray> countByAsync(final JsonObject criteria, final String... groupFields) {
        return Future.succeededFuture(this.countBy(criteria, groupFields));
    }

    // -------------------- Sum Operation ------------
    /*
     * sum(String)
     * sum(String, JsonObject)
     * sum(String, JsonObject, pojo)
     * sumAsync(String)
     * sumAsync(String, JsonObject)
     * sumAsync(String, JsonObject, pojo)
     */
    public BigDecimal sum(final String field) {
        return this.aggregator.sum(field, null);
    }

    public Future<BigDecimal> sumAsync(final String field) {
        return Future.succeededFuture(this.aggregator.sum(field, null));
    }

    public BigDecimal sum(final String field, final JsonObject criteria) {
        return this.aggregator.sum(field, this.workflow.inputQrJ(criteria));
    }

    public Future<BigDecimal> sumAsync(final String field, final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.sum(field, processed)));
    }

    public BigDecimal sum(final String field, final JsonObject criteria, final String pojo) {
        return this.aggregator.sum(field, JqFlow.create(this.analyzer, pojo).inputQrJ(criteria));
    }

    public Future<BigDecimal> sumAsync(final String field, final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.sum(field, processed)));
    }

    /*
     * sumBy(String, JsonObject, String)
     *      <-- sumBy(String, String)
     *      <-- sumByAsync(String, String)
     *      <-- sumByAsync(String, JsonObject, String)
     * sumBy(String, JsonObject, String...)
     *      <-- sumBy(String, String...)
     *      <-- sumByAsync(String, String...)
     *      <-- sumByAsync(String, JsonObject, String...)
     */

    public ConcurrentMap<String, BigDecimal> sumBy(final String field, final JsonObject criteria, final String groupField) {
        return this.aggregator.sum(field, this.workflow.inputQrJ(criteria), groupField);
    }

    public ConcurrentMap<String, BigDecimal> sumBy(final String field, final String groupField) {
        return this.sumBy(field, new JsonObject(), groupField);
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String field, final String groupField) {
        return Future.succeededFuture(this.sumBy(field, new JsonObject(), groupField));
    }

    public Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String field, final JsonObject criteria, final String groupField) {
        return Future.succeededFuture(this.sumBy(field, criteria, groupField));
    }

    public JsonArray sumBy(final String field, final JsonObject criteria, final String... groupFields) {
        return this.aggregator.sum(field, this.workflow.inputQrJ(criteria), groupFields);
    }

    public JsonArray sumBy(final String field, final String... groupFields) {
        return this.sumBy(field, new JsonObject(), groupFields);
    }

    public Future<JsonArray> sumByAsync(final String field, final JsonObject criteria, final String... groupFields) {
        return Future.succeededFuture(this.sumBy(field, criteria, groupFields));
    }

    public Future<JsonArray> sumByAsync(final String field, final String... groupFields) {
        return Future.succeededFuture(this.sumBy(field, new JsonObject(), groupFields));
    }

    // -------------------- Max Operation ------------
    /*
     * max(String)
     * max(String, JsonObject)
     * max(String, JsonObject, pojo)
     * maxAsync(String)
     * maxAsync(String, JsonObject)
     * maxAsync(String, JsonObject, pojo)
     */
    public BigDecimal max(final String field) {
        return this.aggregator.max(field, null);
    }

    public Future<BigDecimal> maxAsync(final String field) {
        return Future.succeededFuture(this.aggregator.max(field, null));
    }

    public BigDecimal max(final String field, final JsonObject criteria) {
        return this.aggregator.max(field, this.workflow.inputQrJ(criteria));
    }

    public Future<BigDecimal> maxAsync(final String field, final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.max(field, processed)));
    }

    public BigDecimal max(final String field, final JsonObject criteria, final String pojo) {
        return this.aggregator.max(field, JqFlow.create(this.analyzer, pojo).inputQrJ(criteria));
    }

    public Future<BigDecimal> maxAsync(final String field, final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.max(field, processed)));
    }

    /*
     * maxBy(String, JsonObject, String)
     *      <-- maxBy(String, String)
     *      <-- maxByAsync(String, String)
     *      <-- maxByAsync(String, JsonObject, String)
     * maxBy(String, JsonObject, String...)
     *      <-- maxBy(String, String...)
     *      <-- maxByAsync(String, String...)
     *      <-- maxByAsync(String, JsonObject, String...)
     */

    public ConcurrentMap<String, BigDecimal> maxBy(final String field, final JsonObject criteria, final String groupField) {
        return this.aggregator.max(field, this.workflow.inputQrJ(criteria), groupField);
    }

    public ConcurrentMap<String, BigDecimal> maxBy(final String field, final String groupField) {
        return this.maxBy(field, new JsonObject(), groupField);
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String field, final String groupField) {
        return Future.succeededFuture(this.maxBy(field, new JsonObject(), groupField));
    }

    public Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String field, final JsonObject criteria, final String groupField) {
        return Future.succeededFuture(this.maxBy(field, criteria, groupField));
    }

    public JsonArray maxBy(final String field, final JsonObject criteria, final String... groupFields) {
        return this.aggregator.max(field, this.workflow.inputQrJ(criteria), groupFields);
    }

    public JsonArray maxBy(final String field, final String... groupFields) {
        return this.maxBy(field, new JsonObject(), groupFields);
    }

    public Future<JsonArray> maxByAsync(final String field, final JsonObject criteria, final String... groupFields) {
        return Future.succeededFuture(this.maxBy(field, criteria, groupFields));
    }

    public Future<JsonArray> maxByAsync(final String field, final String... groupFields) {
        return Future.succeededFuture(this.maxBy(field, new JsonObject(), groupFields));
    }

    // -------------------- Min Operation ------------
    /*
     * min(String)
     * min(String, JsonObject)
     * min(String, JsonObject, pojo)
     * minAsync(String)
     * minAsync(String, JsonObject)
     * minAsync(String, JsonObject, pojo)
     */
    public BigDecimal min(final String field) {
        return this.aggregator.min(field, null);
    }

    public Future<BigDecimal> minAsync(final String field) {
        return Future.succeededFuture(this.aggregator.min(field, null));
    }

    public BigDecimal min(final String field, final JsonObject criteria) {
        return this.aggregator.min(field, this.workflow.inputQrJ(criteria));
    }

    public Future<BigDecimal> minAsync(final String field, final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.min(field, processed)));
    }

    public BigDecimal min(final String field, final JsonObject criteria, final String pojo) {
        return this.aggregator.min(field, JqFlow.create(this.analyzer, pojo).inputQrJ(criteria));
    }

    public Future<BigDecimal> minAsync(final String field, final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.min(field, processed)));
    }

    /*
     * minBy(String, JsonObject, String)
     *      <-- minBy(String, String)
     *      <-- minByAsync(String, String)
     *      <-- minByAsync(String, JsonObject, String)
     * minBy(String, JsonObject, String...)
     *      <-- minBy(String, String...)
     *      <-- minByAsync(String, String...)
     *      <-- minByAsync(String, JsonObject, String...)
     */

    public ConcurrentMap<String, BigDecimal> minBy(final String field, final JsonObject criteria, final String groupField) {
        return this.aggregator.min(field, this.workflow.inputQrJ(criteria), groupField);
    }

    public ConcurrentMap<String, BigDecimal> minBy(final String field, final String groupField) {
        return this.minBy(field, new JsonObject(), groupField);
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String field, final String groupField) {
        return Future.succeededFuture(this.minBy(field, new JsonObject(), groupField));
    }

    public Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String field, final JsonObject criteria, final String groupField) {
        return Future.succeededFuture(this.minBy(field, criteria, groupField));
    }

    public JsonArray minBy(final String field, final JsonObject criteria, final String... groupFields) {
        return this.aggregator.min(field, this.workflow.inputQrJ(criteria), groupFields);
    }

    public JsonArray minBy(final String field, final String... groupFields) {
        return this.minBy(field, new JsonObject(), groupFields);
    }

    public Future<JsonArray> minByAsync(final String field, final JsonObject criteria, final String... groupFields) {
        return Future.succeededFuture(this.minBy(field, criteria, groupFields));
    }

    public Future<JsonArray> minByAsync(final String field, final String... groupFields) {
        return Future.succeededFuture(this.minBy(field, new JsonObject(), groupFields));
    }


    // -------------------- Avg Operation ------------
    /*
     * avg(String)
     * avg(String, JsonObject)
     * avg(String, JsonObject, pojo)
     * avgAsync(String)
     * avgAsync(String, JsonObject)
     * avgAsync(String, JsonObject, pojo)
     */
    public BigDecimal avg(final String field) {
        return this.aggregator.avg(field, null);
    }

    public Future<BigDecimal> avgAsync(final String field) {
        return Future.succeededFuture(this.aggregator.avg(field, null));
    }

    public BigDecimal avg(final String field, final JsonObject criteria) {
        return this.aggregator.avg(field, this.workflow.inputQrJ(criteria));
    }

    public Future<BigDecimal> avgAsync(final String field, final JsonObject criteria) {
        return this.workflow.inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.avg(field, processed)));
    }

    public BigDecimal avg(final String field, final JsonObject criteria, final String pojo) {
        return this.aggregator.avg(field, JqFlow.create(this.analyzer, pojo).inputQrJ(criteria));
    }

    public Future<BigDecimal> avgAsync(final String field, final JsonObject criteria, final String pojo) {
        return JqFlow.create(this.analyzer, pojo).inputQrJAsync(criteria)
            .compose(processed -> Future.succeededFuture(this.aggregator.avg(field, processed)));
    }

    /*
     * avgBy(String, JsonObject, String)
     *      <-- avgBy(String, String)
     *      <-- avgByAsync(String, String)
     *      <-- avgByAsync(String, JsonObject, String)
     * avgBy(String, JsonObject, String...)
     *      <-- avgBy(String, String...)
     *      <-- avgByAsync(String, String...)
     *      <-- avgByAsync(String, JsonObject, String...)
     */

    public ConcurrentMap<String, BigDecimal> avgBy(final String field, final JsonObject criteria, final String groupField) {
        return this.aggregator.avg(field, this.workflow.inputQrJ(criteria), groupField);
    }

    public ConcurrentMap<String, BigDecimal> avgBy(final String field, final String groupField) {
        return this.avgBy(field, new JsonObject(), groupField);
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String field, final String groupField) {
        return Future.succeededFuture(this.avgBy(field, new JsonObject(), groupField));
    }

    public Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String field, final JsonObject criteria, final String groupField) {
        return Future.succeededFuture(this.avgBy(field, criteria, groupField));
    }

    public JsonArray avgBy(final String field, final JsonObject criteria, final String... groupFields) {
        return this.aggregator.avg(field, this.workflow.inputQrJ(criteria), groupFields);
    }

    public JsonArray avgBy(final String field, final String... groupFields) {
        return this.avgBy(field, new JsonObject(), groupFields);
    }

    public Future<JsonArray> avgByAsync(final String field, final JsonObject criteria, final String... groupFields) {
        return Future.succeededFuture(this.avgBy(field, criteria, groupFields));
    }

    public Future<JsonArray> avgByAsync(final String field, final String... groupFields) {
        return Future.succeededFuture(this.avgBy(field, new JsonObject(), groupFields));
    }

    // endregion
}
