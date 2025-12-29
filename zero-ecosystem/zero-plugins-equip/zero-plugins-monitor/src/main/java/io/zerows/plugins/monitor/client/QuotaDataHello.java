package io.zerows.plugins.monitor.client;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.plugins.monitor.QuotaData;

/**
 * @author lang : 2025-12-29
 */
@Monitor("quota-hello")
public class QuotaDataHello implements QuotaData {

    @Override
    public Future<Boolean> register(final JsonObject config, final MeterRegistry registry) {
        
        return Future.succeededFuture(Boolean.TRUE);
    }
}
