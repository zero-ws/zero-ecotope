package io.zerows.extension.module.mbseapi.component;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.extension.module.mbseapi.plugins.JtComponent;
import io.zerows.mbse.metadata.ActIn;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

public class JtChannelDirector extends JtChannelBase {
    /*
     * Adaptor is pure to access dynamic database here, the specification is as:
     * Step 1:
     * - The component defined Database reference, it could be initialized
     * Step 2:
     * - The component defined Mission reference, it could be initialized
     */
    @Override
    public Future<Boolean> initAsync(final JtComponent component, final ActIn request) {
        return Ux.future(this.commercial())
            /*
             * Database initialized
             */
            .compose(JtChannelAnagogic::databaseAsync)
            .compose(database -> Ut.contractAsync(component, Database.class, database))
            /*
             * Mission inited, mount to `JtComponent`
             */
            .compose(dbed -> Ux.future(this.mission()))
            .compose(mission -> Ut.contractAsync(component, Mission.class, mission));
    }
}
