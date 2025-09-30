package io.zerows.extension.runtime.ambient.agent.api.linkage;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Me;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.core.database.jooq.operation.UxJooq;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.ambient.agent.service.linkage.LinkStub;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XLinkageDao;
import io.zerows.extension.runtime.ambient.eon.Addr;
import io.zerows.module.domain.atom.commune.XHeader;
import io.zerows.unity.Ux;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class LinkActor {

    @Inject
    private transient LinkStub stub;

    @Address(Addr.Linkage.FETCH_BY_TYPE)
    public Future<JsonArray> fetchBy(final String type, final XHeader header) {
        return this.stub.fetchByType(type, header.getSigma());
    }

    @Address(Addr.Linkage.FETCH_TARGET)
    public Future<JsonArray> fetchTarget(final String key) {
        return this.stub.fetchNorm(key, null);
    }

    @Address(Addr.Linkage.FETCH_SOURCE)
    public Future<JsonArray> fetchSource(final String key) {
        return this.stub.fetchNorm(null, key);
    }

    @Address(Addr.Linkage.FETCH_ST)
    public Future<JsonArray> fetchSt(final String key) {
        return this.stub.fetchNorm(key, key);
    }

    @Me
    @Address(Addr.Linkage.SAVE_BATCH_B)
    public Future<JsonArray> batchSaveB(final JsonArray data) {
        return this.stub.saving(data, false);
    }

    @Me
    @Address(Addr.Linkage.SAVE_BATCH_V)
    public Future<JsonArray> batchSaveV(final JsonArray data) {
        return this.stub.saving(data, true);
    }

    @Me
    @Address(Addr.Linkage.ADD_NEW_B)
    public Future<JsonObject> addNewB(final JsonObject data) {
        return this.stub.create(data, false);
    }

    @Me
    @Address(Addr.Linkage.ADD_NEW_V)
    public Future<JsonObject> addNewV(final JsonObject data) {
        return this.stub.create(data, true);
    }

    @Me
    @Address(Addr.Linkage.SYNC_B)
    public Future<JsonArray> syncB(final JsonObject request) {
        /*
         * Copy to `data`
         */
        final JsonArray data = Ut.valueJArray(request, KName.DATA);
        final JsonArray removed = Ut.valueJArray(request, "removed");
        Ut.itJArray(data).forEach(json -> {
            Ut.valueCopy(json, request,
                KName.ACTIVE,
                KName.LANGUAGE,
                KName.SIGMA,
                KName.UPDATED_BY,
                KName.UPDATED_AT
            );
            if (!json.containsKey(KName.CREATED_BY)) {
                json.put(KName.CREATED_AT, request.getValue(KName.UPDATED_AT));
                json.put(KName.CREATED_BY, request.getValue(KName.UPDATED_BY));
            }
        });
        return this.stub.syncB(data, removed);
    }

    @Address(Addr.Linkage.FETCH_BY_KEY)
    public Future<JsonObject> byKey(final String key) {
        return Ux.Jooq.on(XLinkageDao.class).fetchJByIdAsync(key);
    }

    @Address(Addr.Linkage.REMOVE_BY_REGION)
    public Future<Boolean> removeKey(final String key, final XHeader header) {
        // Remove by `key` or `region`
        final UxJooq jooq = Ux.Jooq.on(XLinkageDao.class);
        final JsonObject criteria = Ux.whereAnd();
        criteria.put("region", key);
        criteria.put(KName.SIGMA, header.getSigma());
        return jooq.deleteByAsync(criteria);
    }

}
