package io.zerows.cortex;

import io.vertx.core.eventbus.Message;
import io.zerows.epoch.web.Envelop;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Tool for invoker do shared works.
 */
@SuppressWarnings("all")
@Slf4j
public class InvokerUtil {
    public static final String MSG_DIRECT = "[ ZERO ] ( DIRECT ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";
    public static final String MSG_RPC = "[ ZERO ] ( RPC ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";
    public static final String MSG_HANDLE = "[ ZERO ] ( HANDLE ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";

    static Object invokeAsync(final Object proxy,
                              final Method method,
                              final Envelop envelop,
                              final Message<?> message
    ) {
        /*
         * 最早的截断处理，此处的 message 变量会有两个值
         * - 为 null：原始调用核心流程（标准流程）
         * - 非 null: 追加的 void(???, Message<?>) 调用流程
         * 这种流程模式下，必须保证调用者的返回值是 void / Void 类型
         *
         * void Xxx(???, Message<?>)
         */
        if (CallSpec.isRetVoid(method) && Objects.nonNull(message)) {
            // 注意参数信息
            return Invoker.ofAction(InvokerType.MESSAGE).execute(proxy, method, envelop, message);
        }


        /*
         * 直接调用模式，输入参数是 Envelop 的封装对象，且参数为 1
         *
         * Envelop Xxx(Envelop)
         */
        if (CallSpec.isInEnvelop(method)) {
            return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, envelop);
        }


        /*
         * 单参数动态调用模式，参数个数必须是 1
         *
         * Xxx Yyy(???)
         */
        if (1 == method.getParameterCount()) {
            return Invoker.ofAction(InvokerType.ONE_DYNAMIC).execute(proxy, method, envelop);
        }


        /*
         * 标准动态调用模式，默认调用，广域匹配
         */
        return Invoker.ofAction(InvokerType.STANDARD).execute(proxy, method, envelop);
    }

    /**
     * Agent 直接调用模式
     */
    public static <T> T invoke(final Object proxy,
                               final Method method,
                               final Object... args) {
        return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, args);
    }
}
