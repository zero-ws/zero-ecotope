package io.zerows.plugins.monitor.metadata;

/**
 * @author lang : 2025-12-29
 */
public enum MetricType {
    SCOPE,              // 范围处理，如 app, tenant, sigma
    COMPONENT,          // 组件监控
    SERVICE,            // 服务监控
    METRIC,             // 统计指标
    CONFIG,             // 配置监控
}
