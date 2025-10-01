package io.zerows.epoch.bootplus.extension.migration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.access.app.HArk;

public interface MigrateStep {

    MigrateStep bind(final HArk ark);

    /*
     * 升级专用
     */
    Future<JsonObject> procAsync(JsonObject config);
}
