package io.zerows.epoch.corpus.container.uca.gateway;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.platform.annotations.Memory;

interface CACHE {
    @Memory(Aim.class)
    Cc<String, Aim<RoutingContext>> CC_AIMS = Cc.openThread();
}
