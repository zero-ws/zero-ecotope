package io.zerows.cortex;

import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.ParameterBuilder;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import io.zerows.weaver.ZeroType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
class CallSingle implements Invoker.Action {
    private boolean modeInterface(final JsonObject json) {
        final long count = json.fieldNames().stream().filter(Ut::isInteger)
            .count();
        // All json keys are numbers
        log.debug("[ ZERO ] ( Mode ) 是否接口模式：count = {}, json = {}", count, json.encode());
        return count == json.fieldNames().size();
    }

    @Override
    public <T> T execute(final Object proxy, final Method method, final Object... args) {
        final Envelop envelop = (Envelop) args[0];
        final Class<?> argType = method.getParameterTypes()[VValue.IDX];
        // Append single argument
        final ParameterBuilder<Envelop> builder = ParameterBuilder.ofWorker();
        final Object analyzed = builder.build(envelop, argType);
        /*
         * 解决 User = null（未登录）时无法调用的问题
         * - 如果未登录 -> Objects.isNull -> true
         * - 而参数允许为 null -> !allowNull -> false
         * 直接进入 else 分支，而不走 if 分支
         */
        if (Objects.isNull(analyzed) && !ParameterBuilder.allowNull(argType)) {
            // One type dynamic here
            final Object reference = envelop.data();
            // Non Direct
            Object parameters = reference;
            if (JsonObject.class == reference.getClass()) {
                final JsonObject json = (JsonObject) reference;
                if (this.modeInterface(json)) {
                    // Proxy mode
                    if (VValue.ONE == json.fieldNames().size()) {
                        // New Mode for direct type
                        parameters = json.getValue("0");
                    }
                }
            }
            final Object arguments = ZeroType.value(argType, Ut.toString(parameters));
            return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, arguments);
        } else {
            /*
             * XHeader
             * User
             * Session
             * These three argument types could be single
             */
            return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, analyzed);
        }
    }
}
