package io.zerows.extension.mbse.action.plugins;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.bootstrap.JtPin;
import io.zerows.extension.mbse.action.bootstrap.ServiceEnvironment;
import io.zerows.extension.mbse.action.uca.monitor.JtMonitor;
import io.zerows.management.OZeroStore;
import io.zerows.platform.constant.VValue;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Jet动态路由专用配置，此配置为单例模式，整个环境中只会出现一套，它用于构造完整的动态路由配置
 * <pre><code>
 *     vertx-jet.yml
 *
 *     router:
 *        wall: /api
 *        worker:
 *           instances: 64
 *        agent:
 *           instances: 32
 *        unity: io.horizon.spi.environment.UnityAmbient
 * </code></pre>
 *
 * @author lang : 2024-06-26
 */
class JetPolluxOptions {
    // 多应用服务专用环境
    private static final ConcurrentMap<String, ServiceEnvironment> AMBIENT = JtPin.serviceEnvironment();
    private static final AtomicInteger LOG_OPTION = new AtomicInteger(0);
    private static JetPolluxOptions INSTANCE;
    // ------ 实例变量
    private final transient JtMonitor monitor = JtMonitor.create(this.getClass());
    private final transient JsonObject configuration = new JsonObject();

    private JetPolluxOptions() {
        final JsonObject routerJ = OZeroStore.option(YmlCore.router.__KEY);
        this.configuration.mergeIn(routerJ, true);
    }

    static JetPolluxOptions singleton() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JetPolluxOptions();
        }
        return INSTANCE;
    }

    JsonObject inConfiguration() {
        return this.configuration.copy();
    }

    @SuppressWarnings("unchecked")
    Class<OAxis> inComponent() {
        return (Class<OAxis>) OZeroStore.classInject(YmlCore.router.__KEY);
    }

    Set<JtUri> inUri() {
        return AMBIENT.keySet().stream()
            .flatMap(appId -> AMBIENT.get(appId).routes().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * 是否满足 Dynamic 的执行流程，如果不满足则直接跳过，有两处等待配置
     * <pre><code>
     *     1. vertx-inject.yml 中配置 router 节点连接 OAxis 的动态路由
     *     2. ServiceEnvironment 环境信息检查，如果环境信息检查不通过则直接返回 false
     * </code></pre>
     * 新版中这个方法直接在 {@link JetAxisManager} 的 isEnabled 中调用，如果没有配置完成则 isEnabled 返回 false，那么意味着动态
     * 路由并没有开启，这种场景下上层调用则直接跳过。
     *
     * @return 满足 true
     */
    boolean isReady(final HBundle owner) {
        final OLog logger = Ut.Log.configure(this.getClass());
        final String classSelf = this.getClass().getSimpleName();
        if (VValue.ZERO == LOG_OPTION.getAndIncrement()) {
            logger.info(INFO.DYNAMIC_DETECT, classSelf);
        }


        /*
         * OAxis 类名处理和执行，此处类名必须配置在 vertx-inject.yml 中，并且为动态发布路由，新版的 OAxis 接口之下的动态路由模式，
         * 提取 vertx-inject.yml 中的配置检查是否符合 OAxis 路由扩展相关信息
         */
        final Class<?> clazz = OZeroStore.classInject(YmlCore.router.__KEY);
        if (Objects.isNull(clazz) || !Ut.isImplement(clazz, OAxis.class)) {


            if (VValue.ONE == LOG_OPTION.getAndIncrement()) {
                final String className = clazz.getName();
                logger.info(INFO.DYNAMIC_SKIP, classSelf, className);
            }
            return false;           // 跳出：不满足动态路由条件
        }
        if (VValue.ONE == LOG_OPTION.getAndIncrement()) {
            logger.info(INFO.DYNAMIC_FOUND, classSelf, clazz.getName(), this.configuration.encode());
        }


        /*
         * AMBIENT 的监控检查
         */
        this.monitor.agentConfig(this.configuration);
        if (Objects.isNull(AMBIENT) || AMBIENT.isEmpty()) {
            /* 「Failure」 Deployment 失败 */
            this.monitor.workerFailure();
            return false;           // 跳出：不满足动态路由条件
        }

        return true;                // 满足条件
    }
}
