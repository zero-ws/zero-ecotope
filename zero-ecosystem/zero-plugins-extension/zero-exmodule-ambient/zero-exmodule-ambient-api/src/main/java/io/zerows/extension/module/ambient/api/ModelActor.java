package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.extension.module.ambient.service.ModelStub;
import jakarta.inject.Inject;

@Queue
public class ModelActor {

    @Inject
    private transient ModelStub stub;

    @Address(Addr.Module.BY_NAME)
    public Future<JsonObject> fetchModule(
        final String appId,
        final String entry) {
        return this.stub.fetchModule(appId, entry);
    }

    @Address(Addr.Module.MODELS)
    public Future<JsonArray> fetchModels(final String sigma) {
        return this.stub.fetchModels(sigma);
    }

    @Address(Addr.Module.MODEL_FIELDS)
    public Future<JsonArray> fetchAttrs(final String identifier,
                                        final XHeader header) {
        return this.stub.fetchAttrs(identifier, header.getSigma());
    }
}
