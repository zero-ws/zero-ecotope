package io.zerows.extension.module.ambient.monitor;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.platform.management.StoreApp;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.plugins.monitor.metadata.MetricType;
import io.zerows.specification.app.HApp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-12-29
 */
@Monitor(MOM.APP)
public class QuotaMetricApp extends QuotaMetricBase {
    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final StoreApp storeApp = StoreApp.of();
        final List<MetricRow> metric = new ArrayList<>();
        for (final String appId : storeApp.keys()) {
            final HApp app = storeApp.valueGet(appId);
            final MetricRow row = this.buildRow(app);
            metric.add(row);
        }
        return Future.succeededFuture(metric);
    }

    /**
     * 特定属性构造
     *
     * @param app 应用信息
     *
     * @return 指标行
     */
    private MetricRow buildRow(final HApp app) {
        final MetricRow row = new MetricRow().id(app.id())
            .category(MetricType.SCOPE);
        row.name(app.name());
        row.vDisplay(app.ns());
        row.group(app.tenant());    // 租户分组
        row.data(app.data());       // 数据
        row.config(app.option());   // 配置
        row.addrInstance(app.hashCode());   // 应用 HashCode
        return row;
    }

    @Override
    protected String metricName() {
        return MOM.NAME_SCOPED;
    }
}
