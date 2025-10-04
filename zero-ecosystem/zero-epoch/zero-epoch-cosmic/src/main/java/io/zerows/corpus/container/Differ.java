package io.zerows.corpus.container;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.platform.annotations.Memory;

/**
 * Different type for worklow building
 *
 * @param <Context>
 */
public interface Differ<Context> {

    @Memory(Aim.class)
    Cc<String, Aim<RoutingContext>> CC_AIMS = Cc.openThread();

    Aim<Context> build(ActorEvent event);
}
