package io.zerows.plugins.monitor.client;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.plugins.monitor.metadata.MonitorConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-12-29
 */
public abstract class QuotaMetricBase implements QuotaMetric {

    private MultiGauge multiGauge;

    protected abstract Future<List<MetricRow>> metricFrom(JsonObject config, Vertx vertxRef);

    protected abstract String metricName();

    @Override
    public Future<Boolean> register(final JsonObject config, final MeterRegistry registry, final Vertx vertxRef) {
        // 构造基本数据
        final MultiGauge gauge = this.executor(registry);

        return this.metricFrom(config, vertxRef).compose(metrics -> {
            if (Objects.isNull(metrics)) {
                metrics = Collections.emptyList();
            }

            final List<MultiGauge.Row<?>> rows = metrics.stream()
                .filter(Objects::nonNull)
                .map(MetricRow::vRow)
                .collect(Collectors.toList());

            gauge.register(rows);

            return Future.succeededFuture(Boolean.TRUE);
        }).recover(err -> {
            this.log().error("{} 抓取指标失败: metric={}, error={}",
                MonitorConstant.K_PREFIX_MOC, this.metricName(), err.getMessage());
            // Processed
            err.printStackTrace();
            return Future.succeededFuture(Boolean.FALSE);
        });
    }

    protected MultiGauge executor(final MeterRegistry registry) {
        if (Objects.isNull(this.multiGauge)) {
            final String metricName = QuotaValue.QUOTA_NS_PREFIX + this.metricName();
            this.multiGauge = MultiGauge.builder(metricName)
                .description("[ ZERO ] Monitor: " + this.metricName())
                .tag(KName.NAME, this.metricName())
                .register(registry);
            this.log().info("{} --> / 指标容器 MeterRegistry 初始化完成: {}", MonitorConstant.K_PREFIX_MOC, metricName);
        }
        return this.multiGauge;
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
