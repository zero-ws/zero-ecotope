package io.zerows.epoch.configuration;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.annotations.Worker;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.enums.EmDeploy;
import io.zerows.specification.configuration.HSetting;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Vertx 专用节点，清单
 * <pre><code>
 *     1. VertxOptions 一个          x 1
 *     2. DeploymentOptions         x 3 ( 或特殊处理 )
 *        - 默认的 Agent
 *        - 默认的 Worker
 *        - 默认的 Scheduler
 *     3. DeliveryOptions           x 1
 * </code></pre>
 *
 * @author lang : 2025-10-10
 */
@Slf4j
@Data
@Accessors(chain = true, fluent = true)
public class NodeVertx implements Serializable {
    /**
     * 为了同时兼容底层和上层处理，此处不考虑使用 Class<?> 作键值，由于要实现动态部署流程，Class 的信息有可能在配置中心
     * 上线之前并没有加载到环境中，所以此处的 {@link DeploymentOptions} 直接使用类名来配置，这样就可以保证配置的延迟性，
     * 使得配置本身不会受到元数据的影响，不仅如此，新版的 {@link HSetting} 中本身会包含默认值
     * <pre>
     *     1. delivery
     *     2. deployment
     *     3. shared
     * </pre>
     * 但实例中的配置优先级更高，简单说就是 instance 本身在 yml 文件中可直接定制
     */
    private final ConcurrentMap<String, DeploymentOptions> deploymentOptions = new ConcurrentHashMap<>();

    private DeploymentOptions agentOptions;
    private DeploymentOptions workerOptions;

    private final String name;
    private final NodeNetwork networkRef;
    private EmDeploy.Mode mode = EmDeploy.Mode.CONFIG;

    private VertxOptions vertxOptions;
    private DeliveryOptions deliveryOptions;

    private NodeVertx(final String name, final NodeNetwork networkRef) {
        this.name = name;
        this.networkRef = networkRef;
    }

    public static NodeVertx of(final String name, final NodeNetwork networkRef) {
        return new NodeVertx(name, networkRef);
    }

    @CanIgnoreReturnValue
    public NodeVertx agentOptions(final DeploymentOptions agentOptions, final boolean isAppend) {
        if (isAppend) {
            this.agentOptions = this.deploymentOptions(this.agentOptions, agentOptions);
        } else {
            this.agentOptions = agentOptions;
        }
        return this;
    }

    @CanIgnoreReturnValue
    public NodeVertx workerOptions(final DeploymentOptions workerOptions, final boolean isAppend) {
        if (isAppend) {
            this.workerOptions = this.deploymentOptions(this.workerOptions, workerOptions);
        } else {
            this.workerOptions = workerOptions;
        }
        return this;
    }

    public DeploymentOptions deploymentOptions(final DeploymentOptions storeOptions,
                                               final DeploymentOptions secondOptions) {
        if (Objects.isNull(storeOptions)) {
            return secondOptions;
        }
        final JsonObject storeJ = storeOptions.toJson();
        storeJ.mergeIn(secondOptions.toJson(), true);
        return new DeploymentOptions(storeJ);
    }

    public DeploymentOptions deploymentOptions(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "[ ZERO ] 组件发布过程中不可传入空组件！");
        DeploymentOptions options = this.deploymentOptions.get(clazz.getName());
        if (Objects.isNull(options)) {
            options = this.findDefault(clazz);
        }
        // 发布项修正
        setupAdjust(options, clazz, this.mode);
        // 反向更新
        this.deploymentOptions.put(clazz.getName(), options);
        return options;
    }

    public void deploymentOptions(final Class<?> clazz, final DeploymentOptions options) {
        this.deploymentOptions.put(clazz.getName(), options);
    }

    private DeploymentOptions findDefault(final Class<?> clazz) {
        final Agent agent = clazz.getDeclaredAnnotation(Agent.class);
        if (Objects.isNull(agent)) {
            // Worker
            return this.workerOptions;
        } else {
            // Agent
            return this.agentOptions;
        }
    }

    public static void setupAdjust(final DeploymentOptions options, final Class<?> clazz,
                                   final EmDeploy.Mode mode) {
        final Worker worker = clazz.getDeclaredAnnotation(Worker.class);
        if (Objects.nonNull(worker)) {
            /*
             * 如果是 Worker 模式，则强制设置 Worker，但在设置过程中
             * - VIRTUAL_THREAD 为第一优先级，直接配置成此模式则直接忽略
             * - 其他模式一律统一成 Worker
             */
            if (ThreadingModel.VIRTUAL_THREAD != options.getThreadingModel()) {
                options.setThreadingModel(ThreadingModel.WORKER);
            }
            if (0 < worker.instances() && KWeb.DEPLOY.INSTANCES != worker.instances()) {
                options.setInstances(worker.instances());
            }
        }

        final Agent agent = clazz.getDeclaredAnnotation(Agent.class);
        if (Objects.nonNull(agent)) {
            /*
             * 如果是 Agent 模式，则必须是 EVENT_LOOP
             */
            options.setThreadingModel(ThreadingModel.EVENT_LOOP);
            if (0 < agent.instances() && KWeb.DEPLOY.INSTANCES != agent.instances()) {
                options.setInstances(agent.instances());
            }
        }

        if (EmDeploy.Mode.CODE == mode) {
            /*
             * 编程模式优先
             */
            Optional.ofNullable(worker).ifPresent(workerAnnotation -> {
                options.setInstances(workerAnnotation.instances());
                options.setHa(workerAnnotation.ha());
            });
            Optional.ofNullable(agent).ifPresent(agentAnnotation -> {
                options.setInstances(agentAnnotation.instances());
                options.setHa(agentAnnotation.ha());
            });
        }
    }
}
