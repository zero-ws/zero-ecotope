package io.zerows.cosmic;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxJmxMetricsOptions;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Slf4j
class StubVertxMonitor {
    private static final Cc<String, StubVertxMonitor> CC_MONITOR = Cc.openThread();

    private StubVertxMonitor() {
    }

    static StubVertxMonitor of() {
        return CC_MONITOR.pick(StubVertxMonitor::new);
    }

    /**
     * 此方法的特殊点在于
     * <pre>
     *     1. 它在执行 {@link Vertx} 实例创建之前，所以不可以调用 {@link NodeStore#findExtension(Object, String)} 的方法来提取配置
     *        因为这个方法的首参 Object -> {@link Vertx} 实例引用
     *     2. 只能通过 {@link NodeNetwork} 来提取对应的配置
     * </pre>
     *
     * @param options {@link VertxOptions}
     */
    void configure(final VertxOptions options, final NodeNetwork network) {
        final HSetting setting = network.setting();
        final HConfig monitorConfig = setting.extension("monitor");
        if (Objects.isNull(monitorConfig)) {
            return;
        }

        // 加载监控的特殊配置
        final JsonObject optionJ = monitorConfig.options();
        if (Ut.isNil(optionJ)) {
            return;
        }
        log.info("[ MNTR ] 打开监控服务 -> JMX，基本配置：{}", optionJ.encode());
        final JsonObject serverJ = Ut.valueJObject(optionJ, KName.SERVER);

        /*
         * jmx-host: 监控服务域名，默认 localhost
         * jmx-port: 监控服务端口，默认 6088
         * jmx-domain： 监控服务域名空间，默认 io.zerows
         */
        final String host = Ut.valueString(serverJ, "jmx-host", "localhost");
        final int port = Ut.valueInt(serverJ, "jmx-port", 6088);
        final int step = Ut.valueInt(serverJ, "jmx-step", 10);
        log.info("[ MNTR ] JMX 监控服务配置 -> domain = {}, port = {}", host, port);

        // 检查是否包含了监控配置，若没有则初始化一个新的
        // 检查是否已经有了 MetricsOptions，如果没有则创建一个新的 Micrometer 配置
        final MetricsOptions currentMetrics = options.getMetricsOptions();
        final MicrometerMetricsOptions micrometerOptions;

        if (currentMetrics instanceof MicrometerMetricsOptions) {
            micrometerOptions = (MicrometerMetricsOptions) currentMetrics;
        } else {
            micrometerOptions = new MicrometerMetricsOptions();
        }

        // 强制开启核心开关
        micrometerOptions
            .setEnabled(true)           // 开启指标采集
            .setJvmMetricsEnabled(true) // 开启 JVM 指标 (堆内存/GC/线程)
            .setJmxMetricsOptions(new VertxJmxMetricsOptions()
                .setEnabled(true)       // 开启 JMX 注册表
                .setDomain("io.zerows") // 设置你的框架 JMX 域名，此处采用固定域名
                .setStep(step)          // 统计步长
            );

        // 回写到 VertxOptions
        options.setMetricsOptions(micrometerOptions);
        log.info("[ MNTR ] JMX 监控服务已启用，端口：{}，域名：{}，统计步长：{} 秒。", port, host, step);
    }
}
