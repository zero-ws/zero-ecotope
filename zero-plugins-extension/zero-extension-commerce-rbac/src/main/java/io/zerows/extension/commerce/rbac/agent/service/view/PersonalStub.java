package io.zerows.extension.commerce.rbac.agent.service.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SView;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface PersonalStub {

    /*
     * Personal View Processing
     */
    Future<List<SView>> byUser(String resourceId, String ownerId, String location);

    Future<SView> byId(String key);

    Future<SView> create(JsonObject data);

    Future<SView> update(String key, JsonObject data);

    Future<Boolean> delete(Set<String> keys);
}
