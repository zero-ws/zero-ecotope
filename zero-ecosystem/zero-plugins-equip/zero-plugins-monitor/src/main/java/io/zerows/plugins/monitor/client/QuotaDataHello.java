package io.zerows.plugins.monitor.client;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.plugins.monitor.QuotaData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-29
 */
@Monitor("quota-hello")
@Slf4j
public class QuotaDataHello implements QuotaData {

    @Override
    public Future<Boolean> register(final JsonObject config, final MeterRegistry registry,
                                    final Vertx vertxRef) {
        // 1. 验证 Vertx 实例是否传进来了 (控制台看一眼 hash)
        log.info(">>> [ Pure ] 验证启动 | Vertx实例: {} | Registry: {}", vertxRef.hashCode(), registry.getClass().getSimpleName());

        // 2. 注册一个“心跳”指标
        // 这是一个 lambda 函数，Prometheus 每次抓取时，它就会产生一个 0~100 的随机数
        Gauge.builder("pure.verify.value", () -> Math.random() * 100)
            .description("验证专用-随机波动值")
            .tag("env", "dev")
            // 🔥🔥🔥 必须加这一行！🔥🔥🔥
            .strongReference(true)
            .register(registry);

        return Future.succeededFuture(Boolean.TRUE);
    }
}
