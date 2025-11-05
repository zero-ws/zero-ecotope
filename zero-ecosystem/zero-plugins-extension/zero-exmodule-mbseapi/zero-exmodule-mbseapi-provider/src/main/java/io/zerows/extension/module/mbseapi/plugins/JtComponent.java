package io.zerows.extension.module.mbseapi.plugins;

import io.vertx.core.Future;
import io.zerows.mbse.metadata.ActIn;
import io.zerows.mbse.metadata.ActOut;

/*
 * Business component, connect to dao, basic condition:
 */
public interface JtComponent {
    /*
     * Access for ActIn here
     */
    Future<ActOut> transferAsync(ActIn request);
}
