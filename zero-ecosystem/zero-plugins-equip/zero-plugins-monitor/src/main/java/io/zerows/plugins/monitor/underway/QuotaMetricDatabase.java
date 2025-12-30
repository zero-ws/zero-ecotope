package io.zerows.plugins.monitor.underway;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-12-30
 */
@Monitor(MOM.DATABASE)
public class QuotaMetricDatabase extends QuotaMetricBase {
    private final DBMany dbm = DBMany.of();

    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final Set<String> dsSet = this.dbm.keySet();
        final List<MetricRow> metric = new ArrayList<>();
        for (final String ds : dsSet) {
            final DBS dbs = this.dbm.get(ds);
            if (Objects.isNull(dbs)) {
                break;
            }
            final Database database = dbs.getDatabase();
            if (Objects.isNull(database)) {
                break;
            }
            // 配置项
            final MetricRow item = MetricOf.database(ds);
            item.name("\uD83D\uDC2C " + database.getInstance());                                  // 数据库名称
            item.vDisplay(database.getUrl());                                   // 显示数据库连接地址
            item.addrMeta(dbs.hashCode()).addrInstance(database.hashCode());    // 内存地址
            metric.add(item);
        }
        return Future.succeededFuture(metric);
    }

    @Override
    protected String metricName() {
        return MOM.NAME_ENV;
    }
}
