package io.zerows.epoch.store.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Join;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.jooq.DBJx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.Ir;
import io.zerows.epoch.metadata.MMAdapt;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
public final class ADJ {
    private static final Cc<String, ADJ> CC_ADB = Cc.openThread();

    private final DBRef ref;
    private final DBS dbs;
    private final DBJx dbj;

    /**
     * {@link DBLoad} 之后的方法，此处无需再执行任何节点级别的处理
     *
     * @param ref DBRef 引用
     * @param dbs 数据源信息
     */
    private ADJ(final DBRef ref, final DBS dbs) {
        this.dbs = dbs;
        this.ref = ref;
        this.dbj = DBJx.of(this.ref, this.dbs);
    }

    /**
     * {@link DBLoad} 之前的方法，此处要执行 {@link DBLoad} 来构造 {@link DBNode}
     *
     * @param join Join 元信息
     * @param dbs  数据源信息
     */
    private ADJ(final Join join, final DBS dbs) {
        this.dbs = dbs;

        final DBLoad loader = SPI.SPI_DB.loader();
        final DBNode leftNode = loader.configure(join.from(), join.vFrom(), dbs);
        final DBNode rightNode = loader.configure(join.to(), join.vTo(), dbs);

        this.ref = DBRef.of(leftNode, rightNode, Kv.create(join.fromField(), join.toField()));
        this.dbj = DBJx.of(this.ref, dbs);
    }

    static ADJ of(final DBRef ref, final DBS dbs) {
        final String cachedKey = dbs.hashCode() + "@" + ref.hashCode();
        return CC_ADB.pick(() -> new ADJ(ref, dbs), cachedKey);
    }

    static ADJ of(final Join join, final DBS dbs, final Kv<String, String> vectorPojo) {
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
        return this;
    }

    public ADJ alias(final Class<?> vertxDao, final Map<String, String> waitFor) {

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
