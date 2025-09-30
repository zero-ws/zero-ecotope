package io.zerows.extension.runtime.workflow.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExActivity;
import io.zerows.unity.Ux;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ReportService implements ReportStub {
    @Inject
    private transient AclStub aclStub;

    @Override
    public Future<JsonArray> fetchActivity(final String modelKey) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.MODEL_KEY, modelKey);
        return Ux.channel(ExActivity.class, JsonArray::new, stub -> stub.activities(condition));
    }

    @Override
    public Future<JsonArray> fetchActivityByUser(final String modelKey, final String userId) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.MODEL_KEY, modelKey);
        condition.put(KName.CREATED_BY, userId);
        return Ux.channel(ExActivity.class, JsonArray::new, stub -> stub.activities(condition));
    }
}
