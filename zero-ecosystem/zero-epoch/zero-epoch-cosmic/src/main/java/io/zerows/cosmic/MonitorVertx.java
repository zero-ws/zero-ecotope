package io.zerows.cosmic;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Slf4j
class MonitorVertx {
    private static final Cc<String, MonitorVertx> CC_MONITOR = Cc.openThread();

    private MonitorVertx() {
    }

    static MonitorVertx of() {
        return CC_MONITOR.pick(MonitorVertx::new);
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
        final List<MonitorEquip> services = HPI.findMany(MonitorEquip.class);
        if (services.isEmpty()) {
            return;
        }
        log.info("[ MNTR ] 打开监控服务 -> JMX，基本配置：{}", optionJ.encode());
        final JsonObject serverJ = Ut.valueJObject(optionJ, KName.SERVER);
        for (final MonitorEquip service : services) {
            log.info("[ MNTR ] --> 监控组件连接：{}", service.getClass());
            service.registryOption(serverJ, options);
        }
    }
}
