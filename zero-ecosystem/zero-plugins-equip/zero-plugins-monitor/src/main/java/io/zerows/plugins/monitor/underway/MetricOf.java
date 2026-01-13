package io.zerows.plugins.monitor.underway;

import io.zerows.plugins.monitor.metadata.MetricRow;
import io.zerows.plugins.monitor.metadata.MetricType;

/**
 * @author lang : 2025-12-30
 */
public interface MetricOf {
    // 环境信息
    String DATASOURCE = "DataSource";
    String CLUSTER = "Cluster";
    String VERTX = "Vert.x";
    String COMPONENT = "Component";
    String TASK = "Task";
    String MANAGEMENT = "Management";

    static MetricRow cache(final String id) {
        return new MetricRow().id(id).category(MetricType.COMPONENT);
    }

    static MetricRow database(final String id) {
        return new MetricRow().id(id).group(DATASOURCE).category(MetricType.CONFIG);
    }

    static MetricRow vertx(final String id) {
        return new MetricRow().id(id).group(VERTX).category(MetricType.CONFIG);
    }

    static MetricRow task(final String id) {
        return new MetricRow().id(id).group(TASK).category(MetricType.COMPONENT);
    }
}
