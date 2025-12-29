package io.zerows.plugins.monitor.hawtio;

import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxJmxMetricsOptions;
import io.zerows.plugins.monitor.server.MonitorEquipBase;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-29
 */
@Slf4j
public class MonitorEquipHawtIo extends MonitorEquipBase {
    @Override
    public void registryOption(final JsonObject serverJ, final MicrometerMetricsOptions micrometerOptions) {

        final int step = Ut.valueInt(serverJ, "jmx-step", 10);
        // 强制开启核心开关
        micrometerOptions.setJmxMetricsOptions(new VertxJmxMetricsOptions()
            .setEnabled(true)       // 开启 JMX 注册表
            .setDomain("io.zerows") // 设置你的框架 JMX 域名，此处采用固定域名
            .setStep(step)          // 统计步长
        );
    }
}
