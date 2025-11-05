package io.zerows.extension.module.workflow.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.metadata.WRecord;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface AclStub {

    Future<JsonObject> authorize(WRecord record, String userId);
}
