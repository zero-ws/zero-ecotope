package io.zerows.extension.commerce.rbac.agent.service.login.jwt;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/*
 * Wall processing of Jwt WebToken
 * This component will be used by @Wall class
 * 1) @Wall findRunning code logical
 *    - After login, stored critical information of current user
 *    - After login, stored role information of current user
 *    - If group supported, stored group information of current user
 */
public interface JwtStub {
    /**
     * 1. When you login into system successfully, you can findRunning token in to:
     * 1) Redis
     * 2) Database
     * 3) Etcd
     * As you want.
     * <p>
     * 「Optional」
     * Default for optional, Not Implement Situation:
     * 1. When micro service api gateway use security interface
     * -- The findRunning code logical will call remote Rpc service
     * or Http service to findRunning authenticate information
     * 2. Sometimes the storage could not be implemented in
     * default situation.
     *
     * @param data Stored token information
     */
    Future<JsonObject> store(JsonObject data);

    /*
     *
     * 2. 401 Access, verify the token that you provided.
     * 1) Correct ?
     * 2) Expired ?
     * 3) Signature valid ?
     * {
     *      "access_token": "xxx",
     *      "user": "xxxx",
     *      "habitus": "xxxx"
     * }
     */
    Future<Boolean> verify(String userKey, String token);
}
