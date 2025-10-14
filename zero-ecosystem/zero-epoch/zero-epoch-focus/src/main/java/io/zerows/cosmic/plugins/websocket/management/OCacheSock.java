package io.zerows.cosmic.plugins.websocket.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.cosmic.plugins.websocket.Remind;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-04-30
 */
public interface OCacheSock extends OCache<Set<Remind>> {

    Cc<String, OCacheSock> CC_SKELETON = Cc.open();


    static OCacheSock of() {
        return of(null);
    }

    static OCacheSock of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheSockAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheSockAmbiguity(bundle), cacheKey);
    }

    static Set<Remind> entireValue() {
        return CC_SKELETON.values().stream()
            .flatMap(sock -> sock.value().stream())
            .collect(Collectors.toSet());
    }
}
