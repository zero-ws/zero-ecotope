package io.zerows.extension.module.modulat.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.extension.module.modulat.component.Ark;
import io.zerows.plugins.monitor.client.QuotaDataBase;
import io.zerows.plugins.monitor.metadata.MetricBase;
import io.zerows.plugins.monitor.metadata.MetricParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Monitor(MOM.BAG_ADMIN)
public class MNBagAdminData extends QuotaDataBase {
    @Override
    protected Future<List<MetricBase>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final Supervisor<String, JsonObject> bagAdmin = Ark.momBagAdmin();
        final List<MetricBase> metric = new ArrayList<>();
        bagAdmin.keys().stream()
            .map(bagAdmin::value)
            .filter(Objects::nonNull)
            .map(MetricParser.of(MNBagAdminParser::new)::build)
            .forEach(metric::add);
        return Future.succeededFuture(metric);
    }

    @Override
    protected String metricName() {
        return "app-config";
    }
}
