package io.zerows.extension.module.workflow.component.modeling;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.metadata.WRecord;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Respect {
    /*
     * Sync Respect
     * 1 - XLinkage
     * 2 - XAttachment
     */
    Future<JsonArray> syncAsync(JsonArray data, JsonObject params, WRecord record);

    Future<JsonArray> fetchAsync(WRecord record);
}
