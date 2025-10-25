package io.zerows.epoch.store.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.program.R2Mapping;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Pagination;
import io.r2mo.vertx.jooq.AsyncDBContext;
import io.r2mo.vertx.jooq.AsyncMeta;
import io.r2mo.vertx.jooq.DBEx;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.platform.constant.VString;
import org.jooq.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ADB {

    private static final Cc<String, ADB> CC_JOOQ = Cc.openThread();

    // region 基本变量定义和构造函数
    private final DBEx<?> dbe;
    private final AsyncMeta metadata;

    /**
     * 直接新版访问 {@link DBEx} 的入口，之后的内容不再访问
     */
    private <T> ADB(final Class<T> daoCls, final DBS dbs, final R2Vector vector) {
        this.dbe = DBEx.of(daoCls, dbs, vector);
        this.metadata = this.dbe.metadata();
    }

    public ConcurrentMap<String, Class<?>> metaTypes() {
        if (Objects.isNull(this.metadata)) {
            return new ConcurrentHashMap<>();
        }
        return this.metadata.metaTypes();
    }

    public String metaTable() {
        final Table<?> table = this.metadata.metaTable();
        if (Objects.isNull(table)) {
            return "(Unknown)";
        }
        return table.getName();
    }

    public Class<?> metaEntity() {
        return this.metadata.metaEntity();
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
    static ADB of(final Class<?> daoCls, final String filename, final DBS dbs) {
        Objects.requireNonNull(dbs, "[ ZERO ] (Direct模式）传入的数据源不可以为 null");
        final R2Vector vector;
        if (StrUtil.isNotBlank(filename)) {
            vector = MMAdapt.of(filename).vector();
        } else {
            vector = null;
        }
        return new ADB(daoCls, dbs, vector);
    }

    static ADB of(final Class<?> daoCls, final R2Vector vector, final DBS dbs) {
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
    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T update(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateBy(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T update(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateBy(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateByJ(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject updateJ(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().updateByJ(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> updateAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsync(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> updateAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsync(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> updateJAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsyncJ(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> updateJAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().updateByAsyncJ(criteria, data);
    }
    // endregion

    // region（write）按照ID 保存（添加/更新）单条数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T upsert(final Object id, final T updated) {
        return this.<T>dbe().saveBy((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T upsert(final Object id, final JsonObject data) {
        return this.<T>dbe().saveBy((Serializable) id, data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject upsertJ(final Object id, final T updated) {
        return this.<T>dbe().saveByJ((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject upsertJ(final Object id, final JsonObject data) {
        return this.<T>dbe().saveByJ((Serializable) id, data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> upsertAsync(final Object id, final T updated) {
        return this.<T>dbe().saveByAsync((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> upsertAsync(final Object id, final JsonObject data) {
        return this.<T>dbe().saveByAsync((Serializable) id, data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> upsertJAsync(final Object id, final T updated) {
        return this.<T>dbe().saveByAsyncJ((Serializable) id, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> upsertJAsync(final Object id, final JsonObject data) {
        return this.<T>dbe().saveByAsyncJ((Serializable) id, data);
    }
    // endregion

    // region（write）按照查询条件树 保存（添加/更新）单条数据 x 8
    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T upsert(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().saveBy(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T upsert(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveBy(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject upsertJ(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveByJ(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject upsertJ(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveByJ(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> upsertAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);                                                  // Unique Forced
        return this.<T>dbe().saveByAsync(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> upsertAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveByAsync(criteria, data);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final T updated) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveByAsyncJ(criteria, updated);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> upsertJAsync(final JsonObject criteria, final JsonObject data) {
        criteria.put(VString.EMPTY, Boolean.TRUE);
        return this.<T>dbe().saveByAsyncJ(criteria, data);
    }
    // endregion

    // region（write）删除单条数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> T delete(final T entity) {
        return this.<T>dbe().remove(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> T delete(final JsonObject data) {
        return this.<T>dbe().remove(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonObject deleteJ(final T entity) {
        return this.<T>dbe().removeJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonObject deleteJ(final JsonObject data) {
        return this.<T>dbe().removeJ(data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<T> deleteAsync(final T entity) {
        return this.<T>dbe().removeAsync(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<T> deleteAsync(final JsonObject data) {
        return this.<T>dbe().removeAsync(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> deleteJAsync(final T entity) {
        return this.<T>dbe().removeAsyncJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonObject> deleteJAsync(final JsonObject data) {
        return this.<T>dbe().removeAsyncJ(data);
    }
    // endregion

    // region（write）删除批量数据 x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> List<T> delete(final List<T> entity) {
        return this.<T>dbe().remove(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> List<T> delete(final JsonArray data) {
        return this.<T>dbe().remove(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> JsonArray deleteJ(final List<T> entity) {
        return this.<T>dbe().removeJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> JsonArray deleteJ(final JsonArray data) {
        return this.<T>dbe().removeJ(data);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> deleteAsync(final List<T> entity) {
        return this.<T>dbe().removeAsync(entity);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<List<T>> deleteAsync(final JsonArray data) {
        return this.<T>dbe().removeAsync(data);
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> deleteJAsync(final List<T> entity) {
        return this.<T>dbe().removeAsyncJ(entity);
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<JsonArray> deleteJAsync(final JsonArray data) {
        return this.<T>dbe().removeAsyncJ(data);
    }
    // endregion

    // region（write）按照“条件”删除数据 x 6
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Boolean deleteById(final Serializable id) {
        return this.<T>dbe().removeBy(id);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Boolean deleteByIds(final Collection<Serializable> ids) {
        return this.<T>dbe().removeBy(ids);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<Boolean> deleteByIdAsync(final Serializable id) {
        return this.<T>dbe().removeByAsync(id);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<Boolean> deleteByIdsAsync(final Collection<Serializable> ids) {
        return this.<T>dbe().removeByAsync(ids);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<Boolean> deleteByAsync(final JsonObject criteria) {
        return this.<T>dbe().removeByAsync(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Boolean deleteBy(final JsonObject criteria) {                                          // Unique Forced
        return this.<T>dbe().removeBy(criteria);
    }
    // endregion

    // region 查询，存在性检查 x 4
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Boolean existById(final Serializable id) {
        return this.<T>dbe().findExist(id);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<Boolean> existByIdAsync(final Serializable id) {
        return this.<T>dbe().findExistAsync(id);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Boolean exist(final JsonObject criteria) {
        return this.<T>dbe().findExist(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<Boolean> existAsync(final JsonObject criteria) {
        return this.<T>dbe().findExistAsync(criteria);
    }
    // endregion

    // region 查询，分组专用方法 GROUP BY x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, List<T>> group(final String field) {
        return new ConcurrentHashMap<>(this.<T>dbe().findGroupBy(field));
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> ConcurrentMap<String, JsonArray> groupJ(final String field) {
        return new ConcurrentHashMap<>(this.<T>dbe().findGroupByJ(field));
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, List<T>>> groupAsync(final String field) {
        return this.<T>dbe().<String>findGroupByAsync(field)
            // 特殊转换
            .compose(map -> Future.succeededFuture(new ConcurrentHashMap<>(map)));
    }

    // map ❌ ------> executed ✅ ------> map ✅
    public <T> Future<ConcurrentMap<String, JsonArray>> groupJAsync(final String field) {
        return this.<T>dbe().findGroupByAsyncJ(field)
            // 特殊转换
            .compose(map -> Future.succeededFuture(new ConcurrentHashMap<>(map)));
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, List<T>> group(final JsonObject criteria, final String field) {
        return new ConcurrentHashMap<>(this.<T>dbe().findGroupBy(criteria, field));
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> ConcurrentMap<String, JsonArray> groupJ(final JsonObject criteria, final String field) {
        return new ConcurrentHashMap<>(this.<T>dbe().findGroupByJ(criteria, field));
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, List<T>>> groupAsync(final JsonObject criteria, final String field) {
        return this.<T>dbe().findGroupByAsync(criteria, field)
            // 特殊转换
            .compose(map -> Future.succeededFuture(new ConcurrentHashMap<>(map)));
    }

    // map ✅ ------> executed ✅ ------> map ✅
    public <T> Future<ConcurrentMap<String, JsonArray>> groupJAsync(final JsonObject criteria, final String field) {
        return this.<T>dbe().findGroupByAsyncJ(criteria, field)
            // 特殊转换
            .compose(map -> Future.succeededFuture(new ConcurrentHashMap<>(map)));
    }

    // endregion

    // region 计数（所有、条件、分组） x 8
    // map ❌ ------> executed ✅ ------> map ❌
    public Long countAll() {
        return this.dbe().count().orElse(0L);
    }


    // map ❌ ------> executed ✅ ------> map ❌
    public Future<Long> countAllAsync() {
        return this.dbe().countAsync();
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Long count(final JsonObject criteria) {
        return this.<T>dbe().count(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<Long> countAsync(final JsonObject criteria) {
        return this.<T>dbe().countAsync(criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, Long> countBy(final JsonObject criteria, final String groupField) {
        return this.<T>dbe().countBy(criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, Long> countBy(final String groupField) {
        return this.<T>dbe().countBy(groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, Long>> countByAsync(final JsonObject criteria, final String groupField) {
        return this.<T>dbe().countByAsync(criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, Long>> countByAsync(final String groupField) {
        return this.<T>dbe().countByAsync(groupField);
    }

    // endregion

    // region 聚集函数求和 SUM x 8

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> BigDecimal sum(final String field) {
        return this.<T>dbe().sum(field).orElse(BigDecimal.ZERO);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> sumAsync(final String field) {
        return this.<T>dbe().sumAsync(field);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> BigDecimal sum(final String field, final JsonObject criteria) {
        return this.<T>dbe().sum(field, criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> sumAsync(final String field, final JsonObject criteria) {
        return this.<T>dbe().sumAsync(field, criteria);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> sumBy(final String field, final String groupField) {
        return this.<T>dbe().sumBy(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> sumBy(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().sumBy(field, criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String field, final String groupField) {
        return this.<T>dbe().sumByAsync(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> sumByAsync(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().sumByAsync(field, criteria, groupField);
    }

    // endregion

    // region 聚集函数平均值 AVG x 8

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> BigDecimal avg(final String field) {
        return this.<T>dbe().avg(field).orElse(BigDecimal.ZERO);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> avgAsync(final String field) {
        return this.<T>dbe().avgAsync(field);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> BigDecimal avg(final String field, final JsonObject criteria) {
        return this.<T>dbe().avg(field, criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> avgAsync(final String field, final JsonObject criteria) {
        return this.<T>dbe().avgAsync(field, criteria);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> avgBy(final String field, final String groupField) {
        return this.<T>dbe().avgBy(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> avgBy(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().avgBy(field, criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String field, final String groupField) {
        return this.<T>dbe().avgByAsync(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> avgByAsync(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().avgByAsync(field, criteria, groupField);
    }

    // endregion

    // region 聚集函数最大值 MAX x 8

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> BigDecimal max(final String field) {
        return this.<T>dbe().max(field).orElse(BigDecimal.ZERO);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> maxAsync(final String field) {
        return this.<T>dbe().maxAsync(field);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> BigDecimal max(final String field, final JsonObject criteria) {
        return this.<T>dbe().max(field, criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> maxAsync(final String field, final JsonObject criteria) {
        return this.<T>dbe().maxAsync(field, criteria);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> maxBy(final String field, final String groupField) {
        return this.<T>dbe().maxBy(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> maxBy(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().maxBy(field, criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String field, final String groupField) {
        return this.<T>dbe().maxByAsync(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> maxByAsync(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().maxByAsync(field, criteria, groupField);
    }

    // endregion

    // region 聚集函数最小值 MIN x 8

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> BigDecimal min(final String field) {
        return this.<T>dbe().min(field).orElse(BigDecimal.ZERO);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> minAsync(final String field) {
        return this.<T>dbe().minAsync(field);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> BigDecimal min(final String field, final JsonObject criteria) {
        return this.<T>dbe().min(field, criteria);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<BigDecimal> minAsync(final String field, final JsonObject criteria) {
        return this.<T>dbe().minAsync(field, criteria);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> minBy(final String field, final String groupField) {
        return this.<T>dbe().minBy(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> ConcurrentMap<String, BigDecimal> minBy(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().minBy(field, criteria, groupField);
    }

    // map ❌ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String field, final String groupField) {
        return this.<T>dbe().minByAsync(field, groupField);
    }

    // map ✅ ------> executed ✅ ------> map ❌
    public <T> Future<ConcurrentMap<String, BigDecimal>> minByAsync(final String field, final JsonObject criteria, final String groupField) {
        return this.<T>dbe().minByAsync(field, criteria, groupField);
    }

    // endregion
}
