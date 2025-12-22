package io.zerows.extension.module.mbseapi.plugins;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbseapi.boot.JtPin;
import io.zerows.extension.module.mbseapi.boot.ServiceEnvironment;
import io.zerows.extension.module.mbseapi.component.JtMonitor;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Jet动态路由专用配置，此配置为单例模式，整个环境中只会出现一套，它用于构造完整的动态路由配置
 * <pre><code>
 *     旧版：
 *     vertx-jet.yml
 *
 *     router:
 *        wall: /api
 *        worker:
 *           instances: 64
 *        agent:
 *           instances: 32
 *        unity: io.horizon.spi.environment.UnityAmbient
 *
 *     新版更新之后的数据格式：
 *     vertx.yml
 *     metamodel:
 *       router:
 *         path: /api
 *         component: {@link io.zerows.extension.module.mbseapi.plugins.JetPollux}
 *       deployment:
 *         worker:
 *           instances: 64
 *         agent:
 *           instances: 32
 * </code></pre>
 *
 * @author lang : 2024-06-26
 */
@Deprecated
class JetPolluxOptions {
    // 多应用服务专用环境
    private static final ConcurrentMap<String, ServiceEnvironment> AMBIENT = JtPin.serviceEnvironment();
    private static final AtomicInteger LOG_OPTION = new AtomicInteger(0);
    private static JetPolluxOptions INSTANCE;
    // ------ 实例变量
    private final transient JtMonitor monitor = JtMonitor.create(this.getClass());
    private final transient JsonObject configuration = new JsonObject();

    private JetPolluxOptions() {
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

    Set<JtUri> inUri() {
        return AMBIENT.keySet().stream()
            .flatMap(appId -> AMBIENT.get(appId).routes().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
