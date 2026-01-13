package io.zerows.plugins.monitor.underway;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.JobActor;
import io.zerows.cosmic.plugins.job.JobStore;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Monitor(MOM.TASK)
public class QuotaMetricJob extends QuotaMetricBase {
    private final JobStore store = JobActor.ofStore();

    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        if (Objects.isNull(this.store)) {
            return Future.succeededFuture(new ArrayList<>());
        }
        final List<MetricRow> metric = new ArrayList<>();
        final Set<Mission> missionSet = this.store.fetch();
        missionSet.forEach(mission -> {
            // 配置项
            final MetricRow item = MetricOf.task(mission.getName());
            item.vDisplay(mission.getCode());

            final Object proxy = mission.getProxy();
            item.addrMeta(proxy.getClass().hashCode());
            item.addrInstance(proxy.hashCode());
            item.addrStandBy(mission.hashCode());
            item.data(mission.mom());

            metric.add(item);
        });
        return Future.succeededFuture(metric);
    }

    @Override
    protected String metricName() {
        return MOM.NAME_TASK;
    }
}
