package io.zerows.epoch.corpus.container.osgi.service;

import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.annotations.Worker;
import io.zerows.epoch.common.shared.context.KRunner;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.container.store.under.StoreVertx;
import io.zerows.epoch.corpus.container.uca.store.StubLinear;
import io.zerows.epoch.corpus.model.atom.running.RunVertx;
import io.zerows.epoch.enums.VertxComponent;
import io.zerows.epoch.corpus.metadata.osgi.service.EnergyDeployment;
import io.zerows.epoch.corpus.metadata.store.OCacheClass;
import org.osgi.framework.Bundle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 已经发布的内容不再发布，而此处只保存当前 {@link RunVertx} 实例中的相关内容，此处只负责 Agent / Worker 两种，其他两种依旧走一个
 * 旧的流程来处理发布和取消发布
 *
 * @author lang : 2024-07-03
 */
public class EnergyDeploymentService implements EnergyDeployment {
    /**
     * 此处数据结构
     * <pre><code>
     *     1. 键：{@link RunVertx} 的 hashCode
     *     2. 值：所有已经发布的 Verticle 的类全名
     *     一个 Vertx 中可以发布多个 Verticle 的类，为后期扩展做相关准备
     * </code></pre>
     */
    private static final ConcurrentMap<Integer, Set<Class<?>>> DEPLOYED =
        new ConcurrentHashMap<>();

    @Override
    public EnergyDeployment runDeploy(final Bundle owner) {
        // 提取 StoreVertx 中的所有 RunVertx 实例
        final StoreVertx storeVertx = StoreVertx.of(owner);
        // 内部调用
        return this.executeVertx(storeVertx, (runVertx) -> this.deployComponents(owner, runVertx));
    }

    @Override
    public void runUndeploy(final Bundle owner) {
        // 提取 StoreVertx 中的所有 RunVertx 实例
        final StoreVertx storeVertx = StoreVertx.of(owner);
        // 内部调用
        this.executeVertx(storeVertx, (runVertx) -> this.undeployToVertx(owner, runVertx));
    }

    @Override
    public EnergyDeployment runDeploy(final Bundle owner, final Object... containers) {
        return this.executeContainer(containers, runVertx -> this.deployComponents(owner, runVertx));
    }

    @Override
    public EnergyDeployment runDeployPlugins(final Bundle owner, final Object... containers) {
        return this.executeContainer(containers, runVertx -> this.deployPlugins(owner, runVertx));
    }

    @Override
    public void runUndeploy(final Bundle owner, final Object... containers) {
        this.executeContainer(containers, runVertx -> this.undeployToVertx(owner, runVertx));
    }

    @Override
    public void runComponentDeploy(final Bundle owner, final Class<?> deployCls, final Object... containers) {
        if (deployCls.isAnnotationPresent(Agent.class)) {
            // 单 Agent 发布
            this.executeContainer(containers, runVertx -> {
                final StubLinear linear = StubLinear.of(owner, VertxComponent.AGENT);
                linear.runDeploy(deployCls, runVertx);

                DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).add(deployCls);
            });
            return;
        }
        if (deployCls.isAnnotationPresent(Worker.class)) {
            // 单 Worker 发布
            this.executeContainer(containers, runVertx -> {
                final StubLinear linear = StubLinear.of(owner, VertxComponent.WORKER);
                linear.runDeploy(deployCls, runVertx);

                DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).add(deployCls);
            });
        }
    }

    @Override
    public void runComponentUndeploy(final Bundle owner, final Class<?> deployCls, final Object... containers) {
        if (deployCls.isAnnotationPresent(Agent.class)) {
            // 多 Agent 发布
            this.executeContainer(containers, runVertx -> {
                final StubLinear linear = StubLinear.of(owner, VertxComponent.AGENT);
                linear.runUndeploy(deployCls, runVertx);

                DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).remove(deployCls);
            });
            return;
        }
        if (deployCls.isAnnotationPresent(Worker.class)) {
            // 多 Worker 发布
            this.executeContainer(containers, runVertx -> {
                final StubLinear linear = StubLinear.of(owner, VertxComponent.WORKER);
                linear.runUndeploy(deployCls, runVertx);

                DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).remove(deployCls);
            });
        }
    }

    // -------------------- 函数封装专用方法 -----------------------------

    private EnergyDeployment executeVertx(final StoreVertx storeVertx,
                                          final Consumer<RunVertx> consumer) {
        storeVertx.keys().stream().map(storeVertx::valueGet)
            .filter(Objects::nonNull)
            .forEach(consumer);
        return this;
    }

    private EnergyDeployment executeContainer(final Object[] container,
                                              final Consumer<RunVertx> consumer) {
        Arrays.stream(container).forEach(item -> {
            if (item instanceof final StoreVertx storeVertx) {
                this.executeVertx(storeVertx, consumer);
            }
            if (item instanceof final RunVertx runVertx) {
                consumer.accept(runVertx);
            }
        });
        return this;
    }

    // -------------------- 单独发布流程 -----------------------------
    private void deployPlugins(final Bundle owner, final RunVertx runVertx) {
        // Infusion 插件处理
        KRunner.run(() -> this.deployComponents(owner, runVertx, VertxComponent.INFUSION, false), "deployment-infix");

        // Rule 验证规则处理
        KRunner.run(() -> this.deployComponents(owner, runVertx, VertxComponent.CODEX, false), "deployment-codex");
    }

    private void deployComponents(final Bundle owner, final RunVertx runVertx) {
        // Agent 发布流程
        KRunner.run(() -> this.deployComponents(owner, runVertx, VertxComponent.AGENT, true), "deployment-agent");

        // Worker 发布流程
        KRunner.run(() -> this.deployComponents(owner, runVertx, VertxComponent.WORKER, true), "deployment-worker");
    }

    private void deployComponents(final Bundle owner, final RunVertx runVertx, final VertxComponent type,
                                  final boolean cached) {
        final Set<Class<?>> scanClass = this.classPending(runVertx, type, cached);
        final StubLinear linear = StubLinear.of(owner, type);
        scanClass.forEach(scanned -> linear.runDeploy(scanned, runVertx));
        if (cached) {
            // 追加更改 DEPLOYED
            DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).addAll(scanClass);
        }
    }

    private void undeployToVertx(final Bundle owner, final RunVertx runVertx) {
        // Agent 撤销流程
        this.undeployToVertx(owner, runVertx, VertxComponent.AGENT);

        // Worker 撤销流程
        this.undeployToVertx(owner, runVertx, VertxComponent.WORKER);
    }

    private void undeployToVertx(final Bundle owner, final RunVertx runVertx, final VertxComponent type) {
        final Set<Class<?>> deployedClass = DEPLOYED.getOrDefault(runVertx.hashCode(), new HashSet<>());
        final StubLinear linear = StubLinear.of(owner, type);
        deployedClass.forEach(deployed -> linear.runUndeploy(deployed, runVertx));
        // 移除
        DEPLOYED.computeIfAbsent(runVertx.hashCode(), (key) -> new HashSet<>()).removeAll(deployedClass);
        if (DEPLOYED.get(runVertx.hashCode()).isEmpty()) {
            DEPLOYED.remove(runVertx.hashCode());
        }
    }

    // -------------------- 数据抽取专用方法 -----------------------------
    private Set<Class<?>> classPending(final RunVertx runVertx, final VertxComponent type, final boolean cached) {
        // 所有类
        final Set<Class<?>> scanClass = OCacheClass.entireValue(type);
        if (!cached) {
            return new HashSet<>(scanClass);
        }
        // 根据已发布的类计算
        final Set<Class<?>> deployedClass = DEPLOYED.getOrDefault(runVertx.hashCode(), new HashSet<>());
        // 计算最终结果类
        return Ut.elementDiff(scanClass, deployedClass);
    }
}
