package io.zerows.plugins.excel;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * @author lang : 2025-10-31
 */
class ExcelAddOn extends AddOnBase<ExcelClient> {

    private static ExcelAddOn INSTANCE;

    private ExcelAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static ExcelAddOn of(final Vertx vertx, final HConfig config) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ExcelAddOn(vertx, config);
        }
        return INSTANCE;
    }

    static ExcelAddOn of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected ExcelManager manager() {
        return ExcelManager.of();
    }

    @Override
    protected ExcelClient createInstanceBy(final String name) {
        return ExcelClient.createClient(this.vertx(), this.config());
    }
}
