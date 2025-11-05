package io.zerows.extension.module.lbs.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/*
 * L_LOCATION table basic service
 */
public interface LocationStub {
    /*
     * Find Location by Id
     */
    Future<JsonObject> fetchAsync(String locationId);
}
