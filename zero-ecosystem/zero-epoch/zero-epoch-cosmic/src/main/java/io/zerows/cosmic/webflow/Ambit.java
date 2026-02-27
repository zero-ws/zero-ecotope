package io.zerows.cosmic.webflow;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

/**
 * AOP 模式下的处理流程，前置和后置都带特定的处理流程，以保证 AOP 的完整性
 *
 * @author lang : 2024-06-27
 */
public interface Ambit {

    Cc<String, Ambit> CC_SKELETON = Cc.openThread();

    static Ambit of(final Class<?> ambitCls) {
        return CC_SKELETON.pick(() -> Ut.instance(ambitCls), ambitCls.getName());
    }

    Future<Envelop> then(RoutingContext context, Envelop envelop);
}
