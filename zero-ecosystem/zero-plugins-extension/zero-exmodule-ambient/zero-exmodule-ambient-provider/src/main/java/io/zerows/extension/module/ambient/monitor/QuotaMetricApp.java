package io.zerows.extension.module.ambient.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.extension.module.ambient.management.OCacheArk;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricBase;
import io.zerows.plugins.monitor.metadata.MetricConfig;
import io.zerows.specification.app.HArk;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-12-29
 */
@Monitor(MOM.APP)
public class QuotaMetricApp extends QuotaMetricBase {
    @Override
    protected Future<List<MetricBase>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final OCacheArk arkStore = OCacheArk.of();
        final List<MetricBase> metric = new ArrayList<>();
        for (final String appId : arkStore.keys()) {
            final HArk ark = arkStore.valueGet(appId);
            final MetricConfig item = new MetricConfig();
            item.id(ark.app().id());
            item.group("G.App");
            item.name(ark.app().name());
            metric.add(item);
        }
        return Future.succeededFuture(metric);
    }

    @Override
    protected String metricName() {
        return "app";
    }
}
