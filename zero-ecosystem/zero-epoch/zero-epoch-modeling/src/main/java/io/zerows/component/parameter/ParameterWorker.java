package io.zerows.component.parameter;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-04-21
 */
class ParameterWorker implements ParameterBuilder<Envelop> {
    private static final Cc<String, ParameterBuilder<Envelop>> CCT_PARAM = Cc.openThread();

    private ParameterWorker() {
    }

    private static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || Ut.isImplement(paramType, expected);
    }

    static ParameterBuilder<Envelop> of() {
        return CCT_PARAM.pick(ParameterWorker::new);
    }

    @Override
    public Object build(final Envelop envelop, final Class<?> type) {
        final Object returnValue;
        final RoutingContext context = envelop.context();
        if (is(type, XHeader.class)) {
            /*
             * XHeader for
             * - sigma
             * - appId
             * - appKey
             * - lang
             */
            final MultiMap headers = envelop.headers();
            final XHeader header = new XHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else if (is(type, Session.class)) {
            /*
             * RBAC required ( When Authenticate )
             * 1) Provide username / password to get data from remote server.
             * 2) Request temp authorization code ( Required Session ).
             */
            returnValue = envelop.session();
        } else if (is(type, HttpServerRequest.class)) {
            /* HttpServerRequest type */
            returnValue = context.request();
        } else if (is(type, HttpServerResponse.class)) {
            /* HttpServerResponse type */
            returnValue = context.response();
        } else if (is(type, io.vertx.core.Vertx.class)) {
            /* Vertx type */
            returnValue = context.vertx();
        } else if (is(type, EventBus.class)) {
            /* EventBus type */
            returnValue = context.vertx().eventBus();
        } else if (is(type, User.class)) {
            /*
             * User type
             */
            returnValue = envelop.user();
        } else {
            /*
             * EmType handler
             */
            returnValue = null;
        }
        return returnValue;
    }
}
