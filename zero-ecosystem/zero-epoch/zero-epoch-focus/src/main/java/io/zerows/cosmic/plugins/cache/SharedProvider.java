package io.zerows.cosmic.plugins.cache;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * 这种依赖注入的模式只能走 Default 的 Vertx 来实现
 *
 * @author lang : 2025-10-14
 */
public class SharedProvider extends AddOnProvider<SharedClient> {
    public SharedProvider(final AddOn<SharedClient> addOn) {
        super(addOn);
    }
}
