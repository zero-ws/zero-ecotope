package io.zerows.plugins.trash;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-17
 */
class TrashAddOn extends AddOnBase<TrashClient> {

    private static TrashAddOn INSTANCE;

    private TrashAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    public static TrashAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static TrashAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new TrashAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("all")
    public TrashManager manager() {
        return TrashManager.of();
    }

    @Override
    protected TrashClient createInstanceBy(final String name) {
        return TrashClient.createClient(this.vertx(), this.config());
    }
}
