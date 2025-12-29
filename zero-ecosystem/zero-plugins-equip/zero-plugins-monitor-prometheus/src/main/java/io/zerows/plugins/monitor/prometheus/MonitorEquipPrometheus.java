package io.zerows.plugins.monitor.prometheus;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.monitor.server.MonitorEquipBase;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorEquipPrometheus extends MonitorEquipBase {

    @Override
    public void registryOption(final JsonObject serverJ, final MicrometerMetricsOptions micrometerOptions) {
        // 不需要再判断 prometheus-enabled 开关了，依赖存在即开启
        // 当然，你也可以保留开关作为“依赖存在但依然想手动关闭”的后门
        final JsonObject configJ = Ut.valueJObject(serverJ, "monitor-config");
        final int port = Ut.valueInt(configJ, KName.PORT, 9090);
        final String path = Ut.valueString(configJ, KName.PATH, "/metrics");

        micrometerOptions.setPrometheusOptions(new VertxPrometheusOptions()
            .setEnabled(true)
            .setStartEmbeddedServer(true)
            .setEmbeddedServerOptions(new HttpServerOptions().setPort(port))
            .setEmbeddedServerEndpoint(path)
        );

        log.info("[ MNTR ] Prometheus 监控已激活 (依赖驱动) -> http://localhost:{}{}", port, path);
    }
}