package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.Route;
import io.zerows.cortex.AxisSub;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.epoch.web.WebEvent;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-05-04
 */
public class AxisSubUri implements AxisSub {
    @Override
    public void mount(final RunRoute runRoute, final HBundle bundle) {
        final WebEvent event = runRoute.refEvent();
        final Route route = runRoute.instance();
        if (null == event.getMethod()) {
            // Support filter JSR340
            route.path(event.getPath())
                .order(event.getOrder());
        } else {
            route.path(event.getPath())
                .method(event.getMethod())
                .order(event.getOrder());
        }
    }
}
