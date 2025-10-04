package io.zerows.cortex.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.epoch.management.OCache;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-05-03
 */
public interface StoreRouter extends OCache<RunRoute> {

    Cc<String, StoreRouter> CC_SKELETON = Cc.openThread();

    static StoreRouter of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, StoreRouterAmbiguity.class);
        return CC_SKELETON.pick(() -> new StoreRouterAmbiguity(bundle), cacheKey);
    }

    StoreRouter addCurrent(RunRoute runRoute);
}
