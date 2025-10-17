package io.zerows.cosmic.plugins.job;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * @author lang : 2025-10-17
 */
class JobClientProvider extends AddOnProvider<JobClient> {
    JobClientProvider(final AddOn<JobClient> addOn) {
        super(addOn);
    }
}
