package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.platform.annotations.Memory;

/**
 * Different type for worklow building
 *
 * @param <Context>
 */
public interface Differ<Context> {

    @Memory(Aim.class)
    Cc<String, Aim<RoutingContext>> CC_AIMS = Cc.openThread();

    Aim<Context> build(WebEvent event);
}
