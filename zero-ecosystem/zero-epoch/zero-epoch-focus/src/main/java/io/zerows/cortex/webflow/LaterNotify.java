package io.zerows.cortex.webflow;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.annotations.Off;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-04
 */
@Slf4j
final class LaterNotify extends LaterBase<Envelop> {

    LaterNotify(final RoutingContext context) {
        super(context);
    }

    @Override
    public void execute(final Envelop message, final Method hooker) {
        if (Objects.isNull(hooker) || !hooker.isAnnotationPresent(Off.class)) {
            return;
        }

        final Annotation annotation = hooker.getAnnotation(Off.class);
        final String address = Ut.invoke(annotation, "address");
        final String[] addresses = Ut.invoke(annotation, "addresses");
        final Set<String> addrList = new HashSet<>();
        if (Objects.nonNull(addresses) && addresses.length > 0) {
            // addresses 优先
            addrList.addAll(Arrays.asList(addresses));
        } else {
            if (Ut.isNotNil(address)) {
                addrList.add(address);
            }
        }
        if (addrList.isEmpty()) {
            return;
        }

        final EventBus eventBus = this.eventbus();
        addrList.forEach(addr -> {
            log.info("[ ZERO ] \uD83D\uDCE3 广播消息 -> address = {} by method {}", addr, hooker.getName());
            eventBus.publish(addr, message);
        });
    }
}
