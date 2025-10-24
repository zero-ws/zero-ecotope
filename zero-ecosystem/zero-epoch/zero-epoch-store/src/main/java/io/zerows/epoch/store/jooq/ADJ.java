package io.zerows.epoch.store.jooq;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.Ir;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public final class ADJ {
    private static final Cc<Class<?>, ADB> CC_ADB = Cc.open();

    private DBRef ref;
    private final DBS dbs;

    static ADJ of(final DBS dbs) {
        return new ADJ(dbs);
    }

    /**
     * 这种模式下假设 DBRef 已经构建完成，每个 Node 都包含
     * <pre>
     *     - table
     *     - dao
     *     - vector
     * </pre>
     *
     * @param ref 连接引用
     *
     * @return 当前 ADJ 实例
     */
    ADJ configure(final DBRef ref) {
        this.ref = ref;
        return this;
    }

    /**
     * 内部流程，延迟构造，此处 DBRef 只包含了
     * <pre>
     *     - dao
     *     - vector
     * </pre>
     * 最原始的样子，所以需要补全剩余信息
     * FIX-DBE: 二阶段配置方法，内部调用 {@link #configure(DBRef)}
     *
     * @param meta       JOIN 元信息
     * @param vectorPojo 映射文件信息
     *
     * @return 当前引用
     */
    ADJ configure(final OldJoin meta, final Kv<String, String> vectorPojo) {
        final DBNode nodeFrom = meta.forFrom(vectorPojo);
        final DBNode nodeTo = meta.forTo(vectorPojo);
        this.complete(nodeFrom, this.dbs);
        this.complete(nodeTo, this.dbs);
        /*
         * FIX-DBE: 此处应该出现一层转换，将 Join 原信息转换成 kvJoin 部分，而不是直接传入 vectorPojo, vectorPojo 已经被消费，虽然二者数据结构一样，但语义不同
         * 旧代码：return this.configure(DBRef.of(nodeFrom, nodeTo, vectorPojo));
         */
        final Kv<String, String> kvJoin = Kv.create(meta.fromField(), meta.toField());
        return this.configure(DBRef.of(nodeFrom, nodeTo, kvJoin));
    }

    /**
     * 完成左右节点的 ADB 构建，以便后续操作。若有新节点，则直接开新的 API 来实现。
     * <p>
     * 一般在编程过程中，JOIN 操作不被推荐，特别是在分布式环境中使用 JOIN 操作会引发很多问题：
     *
     * <pre>
     * - ⚠️ 数据一致性问题：分布式系统中，数据可能会分布在不同的节点上，JOIN 操作可能导致数据不一致。
     * - 🐢 性能问题：JOIN 操作在分布式环境中可能非常耗时，因为需要跨节点进行数据交换和合并。
     * - 🤔 复杂性增加：JOIN 操作增加了系统的复杂性，难以调试和维护。
     * - 🔥 可扩展性问题：JOIN 操作可能成为系统的瓶颈，限制了系统的可扩展性。
     * </pre>
     *
     * 然而，在某些场景下，JOIN 操作可能是不可避免的。在这种情况下，应该特别注意优化 JOIN 操作，例如：
     *
     * <pre>
     * - 🔍 使用索引：确保参与 JOIN 操作的字段上有适当的索引，以加快查询速度。
     * - 🛠️ 数据分区：将数据合理分区，减少跨节点的数据交换。
     * - 💾 缓存机制：使用缓存机制减少频繁的 JOIN 操作。
     * - ✒️ 查询优化：编写高效的查询语句，避免不必要的 JOIN 操作。
     * </pre>
     *
     * 对于双表 JOIN 操作，可以在当前版本中实现，而对于多表 JOIN 操作，建议留待后期版本实现。
     *
     * <pre>
     * - 📅 当前版本：支持双表 JOIN 操作。
     * - ⏳ 后期版本：计划支持多表 JOIN 操作。
     * </pre>
     *
     * 🚀 为了提高系统的性能和可维护性，尽量避免在分布式环境中使用 JOIN 操作，除非绝对必要。
     *
     * @param dbs 数据库源
     */
    private ADJ(final DBS dbs) {
        this.dbs = dbs;
    }

    @CanIgnoreReturnValue
    private DBNode complete(final DBNode node, final DBS dbs) {
        //        Objects.requireNonNull(dbs, "[ ZERO ] （Join模式）传入的数据源不可以为 null");
        //        final R2Vector vector = node.vector();
        //        /*
        //         * FIX-DBE: 此处直接调用 dao() 来提取，而不是 entity() 提取，entity 是后期完成的
        //         * 旧代码：final Class<?> daoCls = node.entity();
        //         */
        //        final Class<?> daoCls = node.dao();
        //        final ADB adb = ADB.of(daoCls, vector, dbs);
        //        /*
        //         * 反向书写属性值，针对 node 节点执行信息补充
        //         * 1. 已设置：
        //         *    - dao
        //         *    - vector
        //         * 2. 待设置：
        //         *    - entity
        //         *    - table
        //         *    - types
        //         *    - key
        //         */
        //        node.table(adb.metaTable());
        //        node.types(adb.metaTypes());
        //        node.entity(adb.metaEntity());
        //        node.key(UUID.randomUUID().toString());
        //        log.info("[ ZERO ] 最终构造的 node 节点信息：{}", node);
        //        CC_ADB.put(daoCls, adb);
        //        return node;
        return null;
    }

    private String findTable(final Class<?> vertxDao) {
        final ADB adb = CC_ADB.get(vertxDao);
        return adb.metaTable();
    }

    public ADJ alias(final Class<?> vertxDao, final String name, final String alias) {
        this.ref.alias(this.findTable(vertxDao), name, alias);
        return this;
    }

    public ADJ alias(final Class<?> vertxDao, final Map<String, String> waitFor) {
        waitFor.forEach((k, v) -> this.ref.alias(this.findTable(vertxDao), k, v));
        return this;
    }

    // -------------------- Search Operation -----------
    /*
     * searchJAsync(JsonObject)
     * searchJAsync(Qr)
     */
    public Future<JsonObject> searchAsync(final JsonObject params) {
        return null; // searchAsync(toQr(params));
    }

    public Future<JsonObject> searchAsync(final Ir qr) {
        return null; // this.joinder.searchAsync(qr, this.merged);
    }

    /*
     * countAsync(JsonObject)
     * countAsync(Qr)
     */
    public Future<Long> countAsync(final JsonObject params) {
        return null; // countAsync(toQr(params));
    }

    public Future<Long> countAsync(final Ir qr) {
        return null; // this.joinder.countAsync(qr);
    }

    /*
     * 「Sync」Operation
     * fetch(Qr)
     * fetch(JsonObject)
     *
     * 「Async」Standard Api
     * fetchAsync(Qr)
     * fetchAsync(JsonObject)
     */
    public JsonArray fetch(final Ir qr) {
        return null; // this.joinder.searchArray(qr, this.merged);
    }

    public JsonArray fetch(final JsonObject params) {
        return null; // this.fetch(toQr(new JsonObject().types(VName.KEY_CRITERIA, params)));
    }

    public Future<JsonArray> fetchAsync(final Ir qr) {
        return null; // Ut.future(this.fetch(qr));
    }

    public Future<JsonArray> fetchAsync(final JsonObject params) {
        return null; // fetchAsync(toQr(new JsonObject().types(VName.KEY_CRITERIA, params)));
    }

    // -------------------- Crud Operation -----------
    /*
     * Delete Operation Cascade
     * 1) 1 x T1, n x T2
     * 2) 1 x T1, 1 x T2
     * Read Operation Cascade
     * 1) 1 x T1, n x T2
     * 2) 1 x T1, 1 x T2
     * Create/Update Operation
     * 1) 1 x T1 ( Create ), 1 x T2 ( Save )
     * 2) 1 x T1 ( Create ), n x T2 ( Save )
     * 3) 1 x T1 ( Update ), 1 x T2 ( Save )
     * 4) 1 x T1 ( Update ), 1 x T2 ( Save )
     */
    public Future<JsonObject> fetchByIdJAsync(final String key, final String field) {
        return null; // this.joinder.fetchById(key, false, field);
    }

    public Future<JsonObject> fetchByIdAAsync(final String key, final String field) {
        return null; // this.joinder.fetchById(key, true, field);
    }

    public Future<Boolean> removeByIdAsync(final String key) {
        return null; // this.joinder.deleteById(key);
    }

    public Future<JsonObject> insertAsync(final JsonObject data, final String field) {
        return null; // this.joinder.insert(data, field);
    }

    public Future<JsonObject> updateAsync(final String key, final JsonObject data, final String field) {
        return null; // this.joinder.update(key, data, field);
    }
}
