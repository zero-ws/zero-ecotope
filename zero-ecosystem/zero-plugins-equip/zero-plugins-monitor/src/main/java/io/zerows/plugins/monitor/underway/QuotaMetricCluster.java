package io.zerows.plugins.monitor.underway;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-12-30
 */
@Monitor(MOM.CLUSTER)
public class QuotaMetricCluster extends QuotaMetricBase {
    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final NodeNetwork network = NodeStore.ofNetwork();
        final List<MetricRow> metric = new ArrayList<>();
        network.vertxNodes().forEach((name, node) -> {
            final MetricRow row = MetricOf.vertx(name);
            row.name(name);
            row.addrMeta(node.networkRef().hashCode()).addrInstance(node.hashCode());
            metric.add(row);
        });
        return Future.succeededFuture(metric);
    }

    @Override
    protected String metricName() {
        return MOM.NAME_ENV;
    }
}
