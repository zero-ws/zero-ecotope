package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.web.WebEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * SyncAim: Non-Event Bus: Request-Response
 * Refactored to support Future<?> return type from invoke
 */
@Slf4j
public class AimSReply extends AimBase implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build arguments
             */
            final Object[] arguments = this.buildArgs(context, event);

            try {
                /*
                 * Method call
                 * Java reflector to call defined method.
                 */
                final Object result = this.invoke(event, arguments);

                // ---------------------------------------------------------
                // 核心修改开始：统一规范化为 Future<Envelop>
                // ---------------------------------------------------------
                final Future<Envelop> processFuture;

                if (result instanceof final Future<?> resultFuture) {
                    // 情况 A: 方法返回的是 Future (异步结果)
                    // 需要等待 Future 完成，拿到真正的返回值(realResult)后再进行 Envelop 封装
                    processFuture = resultFuture
                        .compose(realResult -> AckFlow.nextT(context, realResult));
                } else {
                    // 情况 B: 方法返回的是普通对象 (同步结果)
                    // 直接进行 Envelop 封装
                    processFuture = AckFlow.nextT(context, result);
                }

                // ---------------------------------------------------------
                // 统一处理最终结果
                // ---------------------------------------------------------
                processFuture.onComplete(dataRes -> {
                    if (dataRes.succeeded()) {
                        // 成功：直接回复 Envelop
                        // 直接回复，AckFlow 内部会处理 Envelop 转换为 HTTP Response 的逻辑
                        AckFlow.reply(context, dataRes.result(), event);
                    } else {
                        // 失败：记录日志并回复错误信息
                        final Throwable cause = dataRes.cause();
                        log.error("[ ZERO ] 调用异常 (Async)：", cause);
                        AckFlow.reply(context, Envelop.failure(cause));
                    }
                });

            } catch (final Throwable ex) {
                /*
                 * Capture Synchronous Exceptions (反射调用本身的异常)
                 */
                final Envelop envelop = Envelop.failure(ex);
                AckFlow.reply(context, envelop);
            }

        }, context, event);
    }
}