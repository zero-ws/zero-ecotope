package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;

/*
 * View seeking interface
 * Fetch default view that will be impact and then the system will get
 * Resource Id here.
 */
public interface ScSeeker {

    String ARG0 = KName.URI;
    String ARG1 = KName.METHOD;
    String ARG2 = KName.SIGMA;

    ScSeeker on(ADB jooq);

    /*
     * Seeker resource by params and return to unique resourceId
     * The ofMain resource should be impact by input params
     */
    Future<JsonObject> fetchImpact(JsonObject params);
}
