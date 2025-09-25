package io.zerows.extension.runtime.integration.uca.updater;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.integration.domain.tables.pojos.IDirectory;

/**
 * 1. KIntegration ID changing
 * 2. StorePath changing
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface StoreUp {

    Future<IDirectory> migrate(IDirectory directory, JsonObject directoryJ);
}
