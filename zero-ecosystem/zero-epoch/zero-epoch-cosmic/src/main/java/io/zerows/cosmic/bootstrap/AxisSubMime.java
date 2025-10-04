package io.zerows.cosmic.bootstrap;

import io.reactivex.rxjava3.core.Observable;
import io.vertx.ext.web.Route;
import io.zerows.cortex.AxisSub;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.platform.constant.VString;
import io.zerows.specification.development.compiled.HBundle;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
public class AxisSubMime implements AxisSub {
    @Override
    public void mount(final RunRoute runRoute, final HBundle bundle) {
        final WebEvent event = runRoute.refEvent();
        final Route route = runRoute.instance();
        // produces
        final Set<MediaType> produces = event.getProduces();
        Observable.fromIterable(produces)
            .map(type -> type.getType() + VString.SLASH + type.getSubtype())
            .subscribe(route::produces).dispose();
        // consumes
        final Set<MediaType> consumes = event.getConsumes();
        Observable.fromIterable(consumes)
            .map(type -> type.getType() + VString.SLASH + type.getSubtype())
            .subscribe(route::consumes).dispose();
    }
}
