package io.zerows.epoch.corpus.container.uca.routing;

import io.vertx.ext.web.Route;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.uca.routing.OAxisSub;
import io.zerows.epoch.corpus.model.running.RunRoute;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-04
 */
public class SubAxisUri implements OAxisSub {
    @Override
    public void mount(final RunRoute runRoute, final Bundle bundle) {
        final ActorEvent event = runRoute.refEvent();
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
