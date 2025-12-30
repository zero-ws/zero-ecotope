package io.zerows.extension.module.modulat.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.modulat.component.Ark;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Monitor(MOM.BAG_ADMIN)
public class QuotaMetricBagAdmin extends QuotaMetricBase {
    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final Supervisor<String, JsonObject> bagAdmin = Ark.momBagAdmin();
        final List<MetricRow> metric = new ArrayList<>();
        bagAdmin.keys().stream()
            .map(bagAdmin::value)
            .filter(Objects::nonNull)
            .map(this::build)
            .forEach(metric::add);
        return Future.succeededFuture(metric);
    }

    private MetricRow build(final JsonObject config) {
        final MetricRow item = new MetricRow();
        item.id(Ut.valueString(config, KName.KEY));
        item.group("G.Config");
        item.name(Ut.valueString(config, KName.CODE));
        return item;
    }

    @Override
    protected String metricName() {
        return "app-config";
    }
}
