package io.zerows.epoch.store.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.syntax.Ir;

import java.util.Map;

@SuppressWarnings("all")
public final class ADJ {

    private final DBRef ref;
    private final DBS dbs;

    private ADJ(final DBRef ref, final DBS dbs) {
        this.ref = ref;
        this.dbs = dbs;
    }

    static ADJ of(final DBRef ref, final DBS dbs) {
        return null;
    }

    static ADJ of(final Join meta, final Kv<String, String> vectorPojo, final DBS dbs) {
        return null;
    }

    public ADJ alias(final Class<?> vertxDao, final String name, final String alias) {
        this.ref.alias(null, name, alias);
        return this;
    }

    public ADJ alias(final Class<?> vertxDao, final Map<String, String> waitFor) {
        waitFor.forEach((k, v) -> this.ref.alias(null, k, v));
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
        return null; // this.fetch(toQr(new JsonObject().put(VName.KEY_CRITERIA, params)));
    }

    public Future<JsonArray> fetchAsync(final Ir qr) {
        return null; // Ut.future(this.fetch(qr));
    }

    public Future<JsonArray> fetchAsync(final JsonObject params) {
        return null; // fetchAsync(toQr(new JsonObject().put(VName.KEY_CRITERIA, params)));
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
