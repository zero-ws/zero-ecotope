package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;

/**
 * 广播接口，用于不同场景之下的广播渠道，广播采用 Bridge 模式，
 * 直接和 {@link io.zerows.launcher.annotations.Off} 挂钩
 *
 * @author lang : 2024-04-04
 */
public interface Later<T> {

    @SuppressWarnings("unchecked")
    static Later<Envelop> ofNotify(final RoutingContext context) {
        return (Later<Envelop>) LaterBase.CCT_LATER.pick(() -> new LaterNotify(context), LaterNotify.class.getName());
    }

    @SuppressWarnings("unchecked")
    static Later<Object> ofSession(final RoutingContext context) {
        return (Later<Object>) LaterBase.CCT_LATER.pick(() -> new LaterSession(context), LaterSession.class.getName());
    }

    void execute(T message, Method hooker);
}
