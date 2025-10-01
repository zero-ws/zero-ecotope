package io.zerows.core.web.container.uca.gateway;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.io.zdk.Aim;
import io.zerows.epoch.annotation.Memory;

interface CACHE {
    @Memory(Aim.class)
    Cc<String, Aim<RoutingContext>> CC_AIMS = Cc.openThread();
}
