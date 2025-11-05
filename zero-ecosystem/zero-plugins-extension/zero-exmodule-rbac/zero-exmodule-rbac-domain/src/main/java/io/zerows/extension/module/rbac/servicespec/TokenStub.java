package io.zerows.extension.module.rbac.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;

/*
 * WebToken exchange interface
 * 1. exchange token by authorization code
 */
public interface TokenStub {

    Future<JsonObject> execute(String clientId, Session session);
}
