package io.zerows.cosmic.plugins.job;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-16
 */
class JobClientAddOn extends AddOnBase<JobClient> {
    private static JobClientAddOn INSTANCE;

    private JobClientAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    public static JobClientAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static JobClientAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new JobClientAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("all")
    public JobClientManager manager() {
        return JobClientManager.of();
    }

    @Override
    protected JobClient createInstanceBy(final String name) {
        return JobClient.createClient(this.vertx(), this.config());
    }
}
