package io.zerows.extension.module.rbac.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;

/**
 * @author lang : 2024-07-11
 */
public interface SmsStub {

    Future<Boolean> send(String sessionId, JsonObject params);

    Future<JsonObject> login(String sessionId, JsonObject params, Session session);
}
