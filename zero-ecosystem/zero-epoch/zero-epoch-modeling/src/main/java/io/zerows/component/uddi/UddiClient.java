package io.zerows.component.uddi;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;

/*
 * Uddi Client
 */
public interface UddiClient {

    UddiClient bind(Vertx vertx);

    UddiClient bind(Method method);

    Future<Envelop> connect(final Envelop envelop);
}
