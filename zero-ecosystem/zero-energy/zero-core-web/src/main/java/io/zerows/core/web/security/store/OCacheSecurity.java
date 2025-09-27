package io.zerows.core.web.security.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.zdk.running.OCache;
import io.zerows.module.security.atom.Aegis;
import org.osgi.framework.Bundle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-22
 */
public interface OCacheSecurity extends OCache<Set<Aegis>> {
    Cc<String, OCacheSecurity> CC_SKELETON = Cc.open();

    static OCacheSecurity of() {
        return of(null);
    }

    static OCacheSecurity of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, OCacheSecurityAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheSecurityAmbiguity(bundle), cacheKey);
    }

    static ConcurrentMap<String, Set<Aegis>> entireWall() {
        final ConcurrentMap<String, Set<Aegis>> walls = new ConcurrentHashMap<>();
        CC_SKELETON.get().values().forEach(self -> walls.putAll(self.valueWall()));
        return walls;
    }

    OCacheSecurity remove(Aegis wall);

    OCacheSecurity add(Aegis wall);

    ConcurrentMap<String, Set<Aegis>> valueWall();
}
