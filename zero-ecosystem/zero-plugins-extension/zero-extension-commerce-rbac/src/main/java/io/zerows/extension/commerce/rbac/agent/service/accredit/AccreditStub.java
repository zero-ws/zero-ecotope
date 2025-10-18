package io.zerows.extension.commerce.rbac.agent.service.accredit;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

/*
 * Uniform interface for authorization workflow join restful api.
 * InJson Data:
 * {
 *      "access_token" : "<WebToken Value>",
 *      "options":{
 *      },
 *      "metadata":{
 *          "uri" : "<Defined Uri>",
 *          "requestUri" : "<Request Uri>",
 *          "method" : "<HTTP Method>"
 *      },
 *      "headers":{
 *          "X-Sigma": "Header Value"
 *      }
 * }
 */
public interface AccreditStub {

    Future<JsonObject> profile(User user);

    Future<JsonObject> resource(JsonObject data);
}
