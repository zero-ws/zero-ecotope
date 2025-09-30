package io.zerows.extension.mbse.action.uca.tunnel;

import io.vertx.core.Future;
import io.zerows.core.database.atom.Database;
import io.zerows.core.util.Ut;
import io.zerows.core.web.mbse.atom.runner.ActIn;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtComponent;
import io.zerows.unity.Ux;

/*
 * Default Adaptor channel for database accessing
 */
public class AdaptorChannel extends AbstractChannel {
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
            .compose(Anagogic::databaseAsync)
            .compose(database -> Ut.contractAsync(component, Database.class, database));
    }
}
