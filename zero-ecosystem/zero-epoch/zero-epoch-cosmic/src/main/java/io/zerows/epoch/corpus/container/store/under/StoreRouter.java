package io.zerows.epoch.corpus.container.store.under;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.running.RunRoute;
import io.zerows.support.Ut;
import io.zerows.epoch.sdk.management.OCache;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-03
 */
public interface StoreRouter extends OCache<RunRoute> {

    Cc<String, StoreRouter> CC_SKELETON = Cc.openThread();

    static StoreRouter of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, StoreRouterAmbiguity.class);
        return CC_SKELETON.pick(() -> new StoreRouterAmbiguity(bundle), cacheKey);
    }

    StoreRouter addCurrent(RunRoute runRoute);
}
