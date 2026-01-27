package io.zerows.cosmic.bootstrap;

import io.r2mo.function.Fn;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.cosmic.exception._40013Exception500ReturnType;
import io.zerows.cosmic.exception._40042Exception500ChannelMulti;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Ipc;
import io.zerows.epoch.web.WebEvent;

import java.lang.reflect.Method;

/**
 * Splitter to getNull executor reference.
 * It will happen in startup of route building to avoid
 * request resource spending.
 * 1. Level 1: Distinguish whether enable EventBus
 * EventBus mode: Async
 * Non-EventBus mode: Sync
 * 2. Level 2: Distinguish the request mode
 * One-Way mode: No response needed. ( Return Type )
 * Request-Response mode: Must require response. ( Return Type )
 * Support modes:
 * 1. AsyncAim: Event Bus: Request-Response
 * 2. SyncAim: Non-Event Bus: Request-Response
 * 3. OneWayAim: Event Bus: One-Way
 * 4. BlockAim: Non-Event Bus: One-Way
 * 5. Vert.x Style Request -> Event -> Response
 * 6. Rpc Style for @Ipc annotation
 */
public class SentryMode {
    /**
     * 一级分流器
     * <pre>
     *     1. 此处 `WebEvent` 中的 proxy 是一个代理调用器，要么是 interface ，要么是 class
     *     2. 首先区分三种核心模式
     *        - {@link Address} 普通异步注解，直接走异步模式 {@link DifferEvent} 处理
     *        - {@link Ipc} RPC 异步注解，走 RPC 模式 {@link DifferIpc} 处理
     *        - 无注解，走普通同步模式 {@link DifferCommon} 处理
     *     3. 三种模式互斥，不能同时存在，否则抛出异常
     *        - annotated = true / rpc = false  -> EventBus Mode
     *        - annotated = false / rpc = true  -> Ipc Mode
     *        - annotated = false / rpc = false -> Non Event Bus Mode
     *        - annotated = true / rpc = true   -> {@link _40042Exception500ChannelMulti} 抛出异常
     * </pre>
     * 三个核心类的处理流程
     * <pre>
     *     1. {@link DifferCommon} 区分
     *        根据方法定义的返回值类型区分同步模式中的单向或双向调用
     *        - Void/void       -> {@link AimType#SYNC_PING}
     *                             单向调用 {@link AimSPing}，返回 204 No Content，不提供返回值
     *        - Other           -> {@link AimType#SYNC_REPLY}
     *                             双向调用 {@link AimSReply}
     *
     *     2. {@link DifferIpc} 区分
     *        这种模式下返回值不能为 void / Void，否则抛出异常 {@link _40013Exception500ReturnType} 异常。
     *        语义：Worker 中的参数来自于 Agent，这种场景下的 Agent 必须带有参数输出，而不能直接将 HTTP 请求参数
     *             填充到 Worker 方法中进行处理，同时也是约束开发人员想要执行内部通信，哪怕是信号量，也要求有返回值
     * </pre>
     *
     * @param event WebEvent 等价于接口定义元数据
     * @return Aim
     */
    public Aim<RoutingContext> distribute(final WebEvent event) {
        // 1. Scan method to check @Address
        final Method method = event.getAction();
        final boolean annotated = method.isAnnotationPresent(Address.class);
        final boolean rpc = method.isAnnotationPresent(Ipc.class);
        // 2. Only one channel enabled
        Fn.jvmKo(rpc && annotated, _40042Exception500ChannelMulti.class, method);

        final Differ<RoutingContext> differ;
        if (annotated) {
            // EventBus Mode for Mode: 1,3,5
            differ = DifferEvent.create();
        } else if (rpc) {

            // Ipc Mode for Mode: 6
            differ = DifferIpc.create();
        } else {

            // Non Event Bus for Mode: 2,4
            differ = DifferCommon.create();
        }
        return differ.build(event);
    }
}
