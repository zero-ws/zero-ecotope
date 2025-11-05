package io.zerows.extension.module.mbseapi.component;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.zerows.extension.module.mbseapi.plugins.JtComponent;
import io.zerows.mbse.metadata.ActIn;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/*
 * Default Adaptor channel for database accessing
 */
public class JtChannelAdaptor extends JtChannelBase {
    /*
     * Adaptor is pure to access dynamic database here, the specification is as:
     * Step 1:
     * - The component defined Database reference, it could be initialized
     */
    @Override
    public Future<Boolean> initAsync(final JtComponent component, final ActIn request) {
        return Ux.future(this.commercial())
            /*
             * Database initialized
             */
            .compose(JtChannelAnagogic::databaseAsync)
            .compose(database -> Ut.contractAsync(component, Database.class, database));
    }
}
