package io.zerows.cosmic.bootstrap;

import io.r2mo.base.web.ForStatus;
import io.r2mo.spi.SPI;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.web.WebEvent;
import io.zerows.support.Ut;

/**
 * BlockAim: Non-Event Bus: One-Way
 */
public class AimSPing extends AimBase implements Aim<RoutingContext> {
    private static final ForStatus STATE = SPI.V_STATUS;

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            // 1. Build TypedArgument
            final Object[] arguments = this.buildArgs(context, event);

            // 2. Method call
            final Object invoked = this.invoke(event, arguments);
            // 3. Resource model building
            final Envelop data;
            /*
             * 旧代码使用：Envelop.success(???) 直接生成
             * Fix：解决 204 的 No Content 问题，只有返回值为 void/Void 时，才返回 204
             *
             * 这里的逻辑针对 @OneWay (Fire-and-Forget) 模式：
             * 1. 该模式下通常方法返回值为 void/Void。
             * 2. 根据 HTTP 协议，无内容的成功响应应使用 204 No Content。
             * 3. 此处强制使用 STATE.ok204() 状态码，表明响应中不应包含业务数据。
             *    (即便 Envelop 中包装了 Boolean.TRUE，在 204 响应中 Client 端也会忽略 Body)
             */
            if (Ut.isBoolean(invoked)) {
                data = Envelop.success(invoked, STATE.ok204());
            } else {
                data = Envelop.success(Boolean.TRUE, STATE.ok204());
            }
            // 4. Process modal
            AckFlow.reply(context, data, event);
        }, context, event);
    }
}
