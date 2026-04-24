package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.XHeader;

public interface UploadStub {

    Future<JsonObject> initSession(JsonObject request, XHeader header);

    Future<JsonObject> sessionStatus(String token, XHeader header);

    Future<JsonObject> uploadChunk(String token, Integer index, Buffer buffer, XHeader header);

    Future<JsonObject> completeSession(String token, XHeader header);

    Future<JsonObject> cancelSession(String token, XHeader header);
}
