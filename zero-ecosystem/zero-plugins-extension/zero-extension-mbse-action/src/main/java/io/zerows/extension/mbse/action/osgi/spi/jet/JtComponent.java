package io.zerows.extension.mbse.action.osgi.spi.jet;

import io.vertx.core.Future;
import io.zerows.epoch.mbse.metadata.ActIn;
import io.zerows.epoch.mbse.metadata.ActOut;

/*
 * Business component, connect to dao, basic condition:
 */
public interface JtComponent {
    /*
     * Access for ActIn here
     */
    Future<ActOut> transferAsync(ActIn request);
}
