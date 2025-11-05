package io.zerows.extension.module.tpl.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.tpl.domain.tables.pojos.MyNotify;
import io.zerows.extension.skeleton.common.enums.OwnerType;

/**
 * @author lang : 2024-04-02
 */
public interface NotifyStub {

    Future<MyNotify> fetchNotify(OwnerType ownerType, String owner);

    Future<MyNotify> saveNotify(OwnerType ownerType, String owner, JsonObject data);
}
