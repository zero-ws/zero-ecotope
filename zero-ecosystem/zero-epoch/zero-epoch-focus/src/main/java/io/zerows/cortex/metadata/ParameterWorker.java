package io.zerows.cortex.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.web.Envelop;

/**
 * @author lang : 2024-04-21
 */
class ParameterWorker implements ParameterBuilder<Envelop> {
    private static final Cc<String, ParameterBuilder<Envelop>> CCT_PARAM = Cc.openThread();

    private ParameterWorker() {
    }

    static ParameterBuilder<Envelop> of() {
        return CCT_PARAM.pick(ParameterWorker::new);
    }

    /**
     * 支持类型表
     * <pre>
     *     - {@link XHeader}
     *     - {@link Session}
     *     - {@link HttpServerRequest}
     *     - {@link HttpServerResponse}
     *     - {@link Vertx}
     *     - {@link EventBus}
     *     - {@link User}
     *     - {@link Message}  / 有值
     * </pre>
     *
     * @param envelop   信封对象
     * @param type      参数类型
     * @param extension 扩展参数
     * @return 参数对象
     */
    @Override
    public Object build(final Envelop envelop, final Class<?> type, final Object... extension) {
        Object returnValue = null;
        final RoutingContext context = envelop.context();
        if (ParameterPre.is(type, XHeader.class)) {
            final MultiMap headers = envelop.headers();
            final XHeader header = new XHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else if (ParameterPre.is(type, Session.class)) {
            returnValue = envelop.session();
        } else if (ParameterPre.is(type, HttpServerRequest.class)) {
            returnValue = context.request();
        } else if (ParameterPre.is(type, HttpServerResponse.class)) {
            returnValue = context.response();
        } else if (ParameterPre.is(type, Vertx.class)) {
            returnValue = context.vertx();
        } else if (ParameterPre.is(type, EventBus.class)) {
            returnValue = context.vertx().eventBus();
        } else if (ParameterPre.is(type, User.class)) {
            returnValue = envelop.user();
        } else if (ParameterPre.is(type, Message.class)) {
            if (0 < extension.length && extension[0] instanceof Message<?>) {
                returnValue = extension[0];
            }
        }
        return returnValue;
    }
}
