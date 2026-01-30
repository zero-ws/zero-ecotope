package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.epoch.assembly.DiProxyInstance;
import io.zerows.epoch.web.WebEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
class SentryPrePure implements Sentry.Pre {
    @Override
    public void verify(final RoutingContext context,
                       final WebRequest wrapRequest, final Object[] parsed) {
        final WebEvent event = wrapRequest.getEvent();
        final Object proxy = event.getProxy();
        final Method method = event.getAction();
        try {
            final Object delegate;
            if (proxy instanceof final DiProxyInstance proxyInstance) {
                // Validation / 动态代理模式
                delegate = proxyInstance.proxy();
            } else {
                // Validation / 直接模式
                delegate = proxy;
            }
            Sentry.of().verifyMethod(delegate, method, parsed);
        } catch (final WebException ex) {
            // Basic validation handler
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
