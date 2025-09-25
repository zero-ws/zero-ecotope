package io.zerows.core.web.container.uca.gateway;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.RoutingContext;
import io.zerows.ams.annotations.Memory;
import io.zerows.core.web.io.zdk.Aim;

interface CACHE {
    @Memory(Aim.class)
    Cc<String, Aim<RoutingContext>> CC_AIMS = Cc.openThread();
}
