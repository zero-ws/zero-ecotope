package io.zerows.extension.mbse.action.uca.tunnel;

import io.vertx.core.Future;
import io.zerows.program.Ux;
import io.zerows.epoch.database.Database;
import io.zerows.epoch.corpus.mbse.atom.runner.ActIn;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.support.Ut;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtComponent;

public class DirectorChannel extends AbstractChannel {
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
            .compose(Anagogic::databaseAsync)
            .compose(database -> Ut.contractAsync(component, Database.class, database))
            /*
             * Mission inited, mount to `JtComponent`
             */
            .compose(dbed -> Ux.future(this.mission()))
            .compose(mission -> Ut.contractAsync(component, Mission.class, mission));
    }
}
