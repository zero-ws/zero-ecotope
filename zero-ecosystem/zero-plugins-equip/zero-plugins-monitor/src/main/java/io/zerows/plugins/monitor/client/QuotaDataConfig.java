package io.zerows.plugins.monitor.client;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.plugins.monitor.QuotaData;
import io.zerows.plugins.monitor.QuotaValue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-29
 */
@Monitor(QuotaValue.QUOTA_DATA_CONFIG)
@Slf4j
public class QuotaDataConfig implements QuotaData {
    @Override
    public Future<Boolean> register(final JsonObject config, final MeterRegistry registry, final Vertx vertxRef) {

        return Future.succeededFuture(Boolean.TRUE);
    }
}
