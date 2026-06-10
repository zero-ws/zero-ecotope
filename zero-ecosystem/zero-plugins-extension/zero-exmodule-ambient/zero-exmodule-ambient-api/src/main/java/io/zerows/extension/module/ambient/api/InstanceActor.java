package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.servicespec.InstanceStub;
import jakarta.inject.Inject;

@Queue
public class InstanceActor {

    @Inject
    private transient InstanceStub instanceStub;

    @Address(Addr.Instance.SEARCH)
    public Future<JsonArray> search(final JsonObject criteria) {
        return this.instanceStub.search(criteria);
    }

    @Address(Addr.Instance.BY_ID)
    public Future<JsonObject> getById(final String key) {
        return this.instanceStub.getById(key);
    }

    @Address(Addr.Instance.UPSERT)
    public Future<JsonObject> upsert(final JsonObject instanceData) {
        return this.instanceStub.upsert(instanceData);
    }

    @Address(Addr.Instance.STATUS_UPDATE)
    public Future<JsonObject> updateStatus(final String key, final JsonObject body) {
        return this.instanceStub.updateStatus(key, body.getString(KName.STATUS));
    }

    @Address(Addr.Instance.DELETE)
    public Future<JsonObject> delete(final String key) {
        return this.instanceStub.delete(key);
    }
}
