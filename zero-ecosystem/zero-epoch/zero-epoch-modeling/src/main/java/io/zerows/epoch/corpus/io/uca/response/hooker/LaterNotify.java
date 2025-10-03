package io.zerows.epoch.corpus.io.uca.response.hooker;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.annotations.Off;
import io.zerows.support.Ut;
import io.zerows.epoch.web.Envelop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-04
 */
final class LaterNotify extends AbstractLater<Envelop> {

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
            this.logger().info("[ Zero ] Broadcasting to address = {0} by method {1}", addr, hooker.getName());
            eventBus.publish(addr, message);
        });
    }
}
