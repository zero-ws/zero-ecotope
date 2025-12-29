package io.zerows.plugins.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.backends.BackendRegistries;
import io.zerows.plugins.monitor.metadata.MonitorConstant;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 客户端连接器，先提取所有的 {@link QuotaData} 组件，数量结构如下
 * <pre>
 *     1. {@link Vertx}-01 --> {@link QuotaMonitor}-01
 *           线程池
 *           {@link YmMonitor.Client}-01 --> {@link QuotaData}-01 x (Thread)
 *           {@link YmMonitor.Client}-02 --> {@link QuotaData}-02 x (Thread)
 *        {@link Vertx}-02 --> {@link QuotaMonitor}-02
 *     2. 消费过程中，直接通过 Vertx.hashCode() 定位到对应的 QuotaMonitor 实例
 *     3. 如果出现了引用相同 {@link YmMonitor.Client} 的角色，则直接使用角色配置添加到新的监控表中
 * </pre>
 *
 * @author lang : 2025-12-29
 */
@Slf4j
class QuotaMonitor {

    private static final Cc<Integer, QuotaMonitor> CC_QUOTA_MONITOR = Cc.open();
    private static final ConcurrentMap<String, Class<?>> CLIENTS = MonitorManager.of().classOf();
    private final Vertx vertxRef;
    private final MeterRegistry meterRegistry = BackendRegistries.getDefaultNow();

    private final Cc<String, QuotaData> quotaMap = Cc.openThread();

    private QuotaMonitor(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
        Objects.requireNonNull(this.meterRegistry);
        Objects.requireNonNull(this.vertxRef);
    }

    static QuotaMonitor of(final Vertx vertx) {
        return CC_QUOTA_MONITOR.pick(() -> new QuotaMonitor(vertx), vertx.hashCode());
    }

    Future<Boolean> startQuota(final YmMonitor monitor) {
        // Classes 提取
        if (CLIENTS.isEmpty()) {
            log.warn("{} 无可用客户端组件，跳过。", MonitorConstant.K_PREFIX_MOC);
            return Future.succeededFuture(Boolean.TRUE);
        }


        // Client 启动
        final List<YmMonitor.Client> clientList = monitor.getClients();
        if (clientList.isEmpty()) {
            log.warn("{} 配置中无客户端定义，跳过。", MonitorConstant.K_PREFIX_MOC);
            return Future.succeededFuture(Boolean.TRUE);
        }


        // Roles 启动
        final List<YmMonitor.Role> roleList = monitor.getRoles();
        if (roleList.isEmpty()) {
            log.warn("{} 配置中无角色实例定义，跳过。", MonitorConstant.K_PREFIX_MOC);
            return Future.succeededFuture(Boolean.TRUE);
        }


        log.info("{} 准备启动监控组件：组件 = {}, 配置 = {}, 角色 = {}", MonitorConstant.K_PREFIX_MOC,
            CLIENTS.size(), clientList.size(), roleList.size());

        final List<Future<Boolean>> waitFor = new ArrayList<>();
        for (final YmMonitor.Client clientConfig : clientList) {

            // 直接跳过无效配置
            if (Objects.isNull(clientConfig.getName())) {
                log.warn("{} 客户端配置缺少名称，跳过处理。", MonitorConstant.K_PREFIX_MOC);
                continue;
            }
            if (!clientConfig.getEnabled()) {
                log.warn("{} ( DISABLED ) 客户端配置 `{}` 未启用，跳过处理。", MonitorConstant.K_PREFIX_MOC, clientConfig.getName());
                continue;
            }

            // 配置不存在
            if (!CLIENTS.containsKey(clientConfig.getName())) {
                log.warn("{} 客户端配置 `{}` 无对应组件，跳过处理。", MonitorConstant.K_PREFIX_MOC, clientConfig.getName());
                continue;
            }

            // 角色实例启动
            for (final YmMonitor.Role roleConfig : roleList) {
                if (!CLIENTS.containsKey(roleConfig.getComponent())) {
                    log.warn("{} 角色实例 `{}` 配置的组件 `{}` 不存在，跳过处理。",
                        MonitorConstant.K_PREFIX_MOC, roleConfig.getId(), roleConfig.getComponent());
                    continue;
                }

                waitFor.add(this.startQuota(clientConfig, roleConfig));
            }
        }
        return Fx.combineB(waitFor);
    }

    private Future<Boolean> startQuota(final YmMonitor.Client client, final YmMonitor.Role role) {
        final Class<?> clientCls = CLIENTS.get(client.getName());
        final QuotaData quotaRef = this.quotaMap.pick(() -> SourceReflect.instance(clientCls), clientCls.getName());
        if (Objects.isNull(this.meterRegistry)) {
            log.warn("{} MeterRegistry 未初始化，跳过关键流程！", MonitorConstant.K_PREFIX_MOC);
            return Future.succeededFuture(Boolean.TRUE);
        }
        log.info("{} QuotaData 组件 `{}` 准备启动，角色实例 `{}`。",
            MonitorConstant.K_PREFIX_MOC, quotaRef.getClass().getName(), role.getId());
        final JsonObject roleConfig = role.getConfig();
        return quotaRef.register(roleConfig, this.meterRegistry);
    }
}
