package io.zerows.plugins.cache;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * @author lang : 2026-01-02
 */
class CachedProvider extends AddOnProvider<CachedClient> {
    CachedProvider(final AddOn<CachedClient> addOn) {
        super(addOn);
    }
}
