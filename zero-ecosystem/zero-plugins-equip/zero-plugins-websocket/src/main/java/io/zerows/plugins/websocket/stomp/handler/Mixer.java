package io.zerows.plugins.websocket.stomp.handler;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.stomp.StompServerHandler;
import io.vertx.ext.stomp.StompServerOptions;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Mixer {

    Cc<String, Mixer> CC_MIXER = Cc.openThread();

    static Mixer instance(final Class<?> clazz, final Object... args) {
        return CC_MIXER.pick(() -> Ut.instance(clazz, args), clazz.getName());
    }

    <T> T mount(StompServerHandler handler, StompServerOptions options);

    default <T> T mount(final StompServerHandler handler) {
        return this.mount(handler, null);
    }
}
