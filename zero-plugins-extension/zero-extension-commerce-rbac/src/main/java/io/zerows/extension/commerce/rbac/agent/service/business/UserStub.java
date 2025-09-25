package io.zerows.extension.commerce.rbac.agent.service.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;

/*
 * Basic user interface
 * 1) Get relations between user / role by user key
 * 2) Get OAuth user account information by user key ( client_id )
 */
public interface UserStub {

    /**
     * create user: SUser and OUser
     */
    Future<JsonObject> createUser(JsonObject params);

    /**
     * delete user including related roles and groups
     */
    Future<Boolean> deleteUser(String userKey);

    // ====================== Login Information =============================

    Future<JsonObject> fetchAuthorized(SUser query);

    // ====================== Information ( By Type ) =======================


    /**
     * Update employee information
     */
    Future<JsonObject> updateInformation(String userId, JsonObject params);

    Future<JsonObject> fetchInformation(String userId);
}
