package io.zerows.plugins.monitor.underway;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.management.OCache;
import io.zerows.plugins.monitor.client.QuotaMetricBase;
import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.plugins.monitor.metadata.MonitorConstant;
import io.zerows.support.Ut;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-12-30
 */
@SuppressWarnings("all")
@Monitor(MOM.CC)
public class QuotaMetricCache extends QuotaMetricBase {

    private final Map<Field, Cc> refs = new HashMap<>();

    @Override
    protected Future<List<MetricRow>> metricFrom(final JsonObject config, final Vertx vertxRef) {
        final Map<Field, Cc> cacheRefs = this.parseRef();
        final List<MetricRow> metrics = new ArrayList<>();
        cacheRefs.forEach((field, cc) -> {
            if (cc.momThread()) {
                // 线程模式
                final JsonArray memoryA = (JsonArray) cc.mom();
                Ut.itJArray(memoryA).forEach(memory -> {
                    // java.lang.NullPointerException
                    if (Ut.isNil(memory)) {
                        return;
                    }
                    final MetricRow metric = this.buildRow(field, memory);
                    metric.group("\uD83D\uDD35 " + this.buildGroup(field));
                    metric.addrMeta(cc.hashCode());
                    metrics.add(metric);
                });
            } else {
                // 非线程模式
                final JsonObject memory = (JsonObject) cc.mom();
                if (Ut.isNotNil(memory)) {
                    final MetricRow metric = this.buildRow(field, memory);
                    metric.group("\uD83D\uDFE2 " + this.buildGroup(field));
                    metric.addrMeta(cc.hashCode());
                    metrics.add(metric);
                }
            }
        });
        this.log().debug("{} --> \uD83D\uDCCA 抓取 Cache 指标数量: {}", MonitorConstant.K_PREFIX_MOC, metrics.size());
        return Future.succeededFuture(metrics);
    }

    private String buildGroup(final Field field) {
        final Class<?> declaringClass = field.getDeclaringClass();
        if (OCache.class.isAssignableFrom(declaringClass)) {
            return MetricOf.MANAGEMENT;
        }
        return MetricOf.COMPONENT;
    }

    private MetricRow buildRow(final Field field, final JsonObject memory) {
        final String key = Ut.valueString(memory, KName.TYPE);
        final MetricRow metric = MetricOf.cache(key);
        metric.name(Ut.valueString(memory, KName.KEY));
        if (memory.containsKey(KName.SIZE)) {
            metric.vCount(Ut.valueInt(memory, KName.SIZE));
        }
        if (memory.containsKey("hash")) {
            metric.addrInstance(Ut.valueInt(memory, "hash"));
        }
        final Class<?> declaringClass = field.getDeclaringClass();
        metric.data("sourceClass", declaringClass.getName());
        metric.data("sourceField", field.getName());
        return metric;
    }

    private Map<Field, Cc> parseRef() {
        if (!refs.isEmpty()) {
            return refs;
        }
        this.refs.putAll(MetricMeta.mapOfCc());
        return this.refs;
    }

    @Override
    protected String metricName() {
        return MOM.NAME_CACHE;
    }
}
