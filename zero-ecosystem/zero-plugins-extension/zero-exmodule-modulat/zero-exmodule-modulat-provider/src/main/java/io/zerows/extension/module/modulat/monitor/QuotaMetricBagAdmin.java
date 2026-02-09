package io.zerows.extension.module.modulat.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.modulat.component.Ark;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.plugins.monitor.metadata.MetricType;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.List;

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
            // Fix: java.lang.NullPointerException 配置为空的异常
            .filter(Ut::isNotNil)
            .map(this::build)
            .forEach(metric::add);
        return Future.succeededFuture(metric);
    }

    private MetricRow build(final JsonObject config) {
        final MetricRow item = new MetricRow();
        final String vId = Ut.vId(config);
        item.id(vId);
        item.group("G.Config");
        item.name(Ut.valueString(config, KName.CODE));
        /*
         * Cannot invoke "io.zerows.plugins.monitor.metadata.MetricType.name()"
         * because the return value of "io.zerows.plugins.monitor.metadata.MetricRow.category()" is null
         */
        item.category(MetricType.CONFIG);
        return item;
    }

    @Override
    protected String metricName() {
        return "app-config";
    }
}
