package io.zerows.epoch.corpus.web.websocket.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.web.websocket.atom.Remind;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.management.OCache;
import org.osgi.framework.Bundle;

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

    static OCacheSock of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheSockAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheSockAmbiguity(bundle), cacheKey);
    }

    static Set<Remind> entireValue() {
        return CC_SKELETON.get().values().stream()
            .flatMap(sock -> sock.value().stream())
            .collect(Collectors.toSet());
    }
}
