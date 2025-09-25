package io.zerows.extension.runtime.workflow.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface AclStub {

    Future<JsonObject> authorize(WRecord record, String userId);
}
