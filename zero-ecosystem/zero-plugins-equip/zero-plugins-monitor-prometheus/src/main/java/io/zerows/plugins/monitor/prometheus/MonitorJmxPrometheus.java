package io.zerows.plugins.monitor.prometheus;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.monitor.metadata.MonitorConstant;
import io.zerows.plugins.monitor.metadata.MonitorType;
import io.zerows.plugins.monitor.server.MonitorJmxConnector;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-29
 */
@Slf4j
@SPID("MNTR/PROMETHEUS")
public class MonitorJmxPrometheus implements MonitorJmxConnector {
    @Override
    public boolean isMatch(final MonitorType required) {
        return MonitorType.PROMETHEUS_GRAFANA == required;
    }

    @Override
    public Future<Boolean> startAsync(final JsonObject config, final Vertx vertxRef) {
        // 1. 获取配置 (注意：这里其实是"读取"配置，而不是"设置"，因为设置早就生效了)
        // 务必确保这里的默认值和 MonitorEquipPrometheus 里的默认值完全一致！
        final int port = config.getInteger(KName.PORT, 9090);
        final String path = config.getString(KName.PATH, "/metrics");

        // 2. 打印高亮日志 (提升开发者体验的核心)
        // 使用这种格式，让用户一眼就能看到核心信息
        final String url = "http://localhost:" + port + path;

        log.info("{} Prometheus Metrics 接口已就绪", MonitorConstant.K_PREFIX_MON);
        log.info("{} --> 🔗 Endpoint : {}", MonitorConstant.K_PREFIX_MON, String.format("%-34s", url)); // 格式化对齐

        // 3. (进阶推荐) 简单的配置一致性校验
        // 比如：检查一下是否有人把 port 配置成了 null 或负数
        if (port <= 0) {
            log.warn("{} ⚠️ 检测到 Prometheus 端口配置异常 ({})，请检查配置文件！", MonitorConstant.K_PREFIX_MON, port);
            // 这里通常不阻断 Future，因为 Vert.x 可能使用了默认端口成功启动了
        }
        return Future.succeededFuture(Boolean.TRUE);
    }
}
