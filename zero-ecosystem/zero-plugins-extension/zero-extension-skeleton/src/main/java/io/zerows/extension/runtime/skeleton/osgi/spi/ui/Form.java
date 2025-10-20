package io.zerows.extension.runtime.skeleton.osgi.spi.ui;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Form {
    /*
     * {
     *     "dynamic": false,
     *     "code": form code / form path,
     *     "sigma": ""
     * }
     */
    Future<JsonObject> fetchUi(JsonObject params);
}
