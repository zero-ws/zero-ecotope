package io.zerows.core.web.invocation.micro.uddi;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.core.web.model.commune.Envelop;

import java.lang.reflect.Method;

/*
 * Uddi Client
 */
public interface UddiClient {

    UddiClient bind(Vertx vertx);

    UddiClient bind(Method method);

    Future<Envelop> connect(final Envelop envelop);
}
