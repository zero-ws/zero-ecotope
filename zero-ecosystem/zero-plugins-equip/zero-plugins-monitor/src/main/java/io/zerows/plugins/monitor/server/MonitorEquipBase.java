package io.zerows.plugins.monitor.server;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.zerows.cosmic.MonitorEquip;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控专用设置组件
 *
 * @author lang : 2025-12-29
 */
public abstract class MonitorEquipBase implements MonitorEquip {
    @Override
    public void registryOption(final JsonObject serverJ, final VertxOptions options) {
        /*
         * jmx-host: 监控服务域名，默认 localhost
         * jmx-port: 监控服务端口，默认 6088
         * jmx-domain： 监控服务域名空间，默认 io.zerows
         */
        final String host = Ut.valueString(serverJ, "jmx-host", "localhost");
        final int port = Ut.valueInt(serverJ, "jmx-port", 6088);
        this.log().info("[ MNTR ] JMX 监控服务配置 -> domain = {}, port = {}", host, port);
        // ---------------------------------------------------------------
        // 1. 初始化 Micrometer 配置容器
        // ---------------------------------------------------------------
        final MetricsOptions currentMetrics = options.getMetricsOptions();
        final MicrometerMetricsOptions micrometerOptions;
        if (currentMetrics instanceof MicrometerMetricsOptions) {
            micrometerOptions = (MicrometerMetricsOptions) currentMetrics;
        } else {
            micrometerOptions = new MicrometerMetricsOptions();
        }

        // 基础全局开关 (必须开启，否则后面都不生效)
        micrometerOptions
            .setEnabled(true)
            .setJvmMetricsEnabled(true); // 建议开启，看内存/GC很有用

        this.registryOption(serverJ, micrometerOptions);
        
        options.setMetricsOptions(micrometerOptions);
        this.log().info("[ MNTR ] JMX 监控服务已启用，端口：{}，域名：{}。", port, host);
    }

    private Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected abstract void registryOption(final JsonObject serverJ, final MicrometerMetricsOptions options);
}
