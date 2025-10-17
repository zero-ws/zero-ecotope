package io.zerows.plugins.trash;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * @author lang : 2025-10-17
 */
class TrashProvider extends AddOnProvider<TrashClient> {
    public TrashProvider(final AddOn<TrashClient> addOn) {
        super(addOn);
    }
}
