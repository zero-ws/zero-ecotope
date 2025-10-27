package io.zerows.cosmic.plugins.security.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.metadata.security.KSecurity;
import io.zerows.platform.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-22
 */
public interface OCacheSecurity extends OCache<Set<KSecurity>> {
    Cc<String, OCacheSecurity> CC_SKELETON = Cc.open();

    static OCacheSecurity of() {
        return of(null);
    }

    static OCacheSecurity of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, OCacheSecurityAmbiguity.class);
        return CC_SKELETON.pick(() -> new OCacheSecurityAmbiguity(bundle), cacheKey);
    }

    static ConcurrentMap<String, Set<KSecurity>> entireWall() {
        final ConcurrentMap<String, Set<KSecurity>> walls = new ConcurrentHashMap<>();
        CC_SKELETON.values().forEach(self -> walls.putAll(self.valueWall()));
        return walls;
    }

    OCacheSecurity remove(KSecurity wall);

    OCacheSecurity add(KSecurity wall);

    ConcurrentMap<String, Set<KSecurity>> valueWall();
}
