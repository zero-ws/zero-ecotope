package io.zerows.extension.module.rbac.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermSet;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 * PERM_SET table management processing
 * 1) The code logical for permission fetching
 * 2) The code logical of major definition
 */
public interface RightsStub {
    /*
     * Get permission groups service
     * There are two fields that stored data
     *
     * group - Permission Group
     * identifier - Model Identifier
     */
    Future<JsonArray> fetchAsync(String sigma);

    /*
     * Permission Sync with `group` provided
     * {
     *     "group": "xxx",
     *     "data": []
     * }
     */
    Future<JsonArray> saveDefinition(JsonArray permission, SPermSet permSet);

    /**
     * Update role perm relation information
     */
    Future<JsonArray> saveRoles(String roleId, JsonArray data);

    /*
     * delete by role id
     */
    Future<Boolean> removeRoles(String roleId);
}
