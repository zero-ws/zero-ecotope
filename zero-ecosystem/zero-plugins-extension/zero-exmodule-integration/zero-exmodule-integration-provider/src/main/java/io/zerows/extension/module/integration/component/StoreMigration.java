package io.zerows.extension.module.integration.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.integration.domain.tables.pojos.IDirectory;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class StoreMigration implements StoreUp {
    @Override
    public Future<IDirectory> migrate(final IDirectory directory, final JsonObject directoryJ) {
        // Migration for `integrationId` changing
        return Ux.future(directory);
    }
}
