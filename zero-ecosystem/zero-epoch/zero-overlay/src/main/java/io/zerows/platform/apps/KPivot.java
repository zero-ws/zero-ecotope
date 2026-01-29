package io.zerows.platform.apps;

import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HLot;
import io.zerows.specification.atomic.HBelong;
import io.zerows.specification.cloud.HFrontier;
import io.zerows.specification.cloud.HGalaxy;
import io.zerows.specification.cloud.HSpace;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.spi.HPI;
import io.zerows.support.base.UtBase;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 上下文环境桥
 * <pre><code>
 *     1. 针对 {@link HRegistry} 的桥接封装
 *     2. 从 SPI 中提取 HRegistry，若无法找到则执行默认流程（Min）方式处理
 * </code></pre>
 *
 * @author lang : 2023-06-06
 */
public class KPivot<T> {
    private static final Cc<Integer, KPivot<?>> CC_PIVOT = Cc.open();
    /**
     * {@link HAmbient} 中存储了当前运行的上下文环境，该上下文环境中会包含对应的核心运行信息
     * 此信息可以替换原始架构中的主架构
     * <pre>
     *     废弃的部分
     *     1. Core,     KApp / KTenant
     *     2. zero-jet, Ambient / AmbientEnvironment / JtApp
     *     替换结构
     *     1. {@link HAmbient}
     *        key = {@link HArk}
     *               - {@link HApp}
     *               - {@link io.r2mo.base.dbe.DBS}
     *                  - {@link io.r2mo.base.dbe.Database}
     *               - {@link HBelong}
     *                  - {@link HFrontier}
     *                  - {@link HGalaxy}
     *                  - {@link HSpace}
     *     2. {@link HRegistry}
     *        当前运行的 {@link HAmbient} 为一个单独的实例，也运行在单节点中，不同架构之下节点的性质根据 {@link EmApp} 来区分
     * </pre>
     * 模式相关的配置位于扩展配置中 vertx.yml
     * <pre>
     *     extension:
     *       mode: {@link EmApp}
     *        - 第一管理端/业务管理 {@link EmApp.Mode#CUBE}         app x 1 , tenant x 1    ( 默认值 )
     *        - 第一管理端/应用管理 {@link EmApp.Mode#SPACE}        app x N , tenant x 1
     *        - 第二管理端/租户管理 {@link EmApp.Mode#GALAXY}       app x N , tenant x N
     *     app:
     *       id:
     *       ns:
     *       tenant:
     * </pre>
     * zero-exmodule-ambient 在启动时会初始化当前运行的 {@link HAmbient} 对象，而此时绑定的 {@link HAmbient} 永远只有一个，所以追加的新版本在
     * 此处执行启动连接
     * <pre>
     *     - X_APP      / 应用连接
     *     - X_TENANT   / 租户连接
     * </pre>
     */
    private static HAmbient RUNNING;
    private final T container;
    private final HRegistry<T> context;

    private final HRegistry<T> extension;

    @SuppressWarnings("unchecked")
    private KPivot(final T container) {
        this.container = container;
        this.context = new RegistryCommon<>();
        this.extension = (HRegistry<T>) HPI.findOneOf(HRegistry.class);
    }

    public static HAmbient running() {
        synchronized (KPivot.class) {
            return RUNNING;
        }
    }

    /**
     * 每个容器对象对应一个 KPivot 实例的环境桥
     *
     * @param container 容器对象
     * @param <T>       容器泛型
     * @return 环境桥
     */
    @SuppressWarnings("unchecked")
    public static <T> KPivot<T> of(final T container) {
        Objects.requireNonNull(container, "[ ZERO ] 容器对象不能为 null !");
        return (KPivot<T>) CC_PIVOT.pick(() -> new KPivot<>(container), System.identityHashCode(container));
    }

    private static Set<HArk> combine(final Set<HArk> sources, final Set<HArk> extensions) {
        sources.forEach(source -> {
            // 1. 先做租户过滤
            final HLot owner = source.owner();
            final List<HArk> ownerList = extensions.stream()
                .filter(item -> item.owner().equals(owner))
                .collect(Collectors.toList());

            // 2. 再做二次查找到唯一记录
            final HArk found = UtBase.elementFind(ownerList,
                item -> source.app().equals(item.app()));
            if (Objects.nonNull(found)) {
                source.apply(found);
            }
        });
        // 3. 构造线程安全的集合
        return Collections.synchronizedSet(sources);
    }

    /**
     * 环境检查
     * <pre>
     *     extension:
     *       environment: NORM
     *       mode: CUBE
     * </pre>
     * 前置条件
     * <pre>
     *     1. 如果是 null 则表示纯容器模式
     *     2. 如果非 null 则执行扩展注册流程
     *     3. 根据 mode（默认 CUBE）执行不同的 {@link HAmbient} 初始化
     * </pre>
     * 纯容器模式下不配置 extension 部分，也不会执行任何 {@link HRegistry} 注册程序，应用管理过程中纯容器模式不依赖
     * appId / tenantId 等维度信息，自我注册实现即可。
     *
     * @param config 配置
     * @return 异步环境桥集合
     */
    public Future<Set<HArk>> registryAsync(final HConfig config) {
        if (Objects.isNull(config)) {
            return Future.succeededFuture(new HashSet<>());
        }
        // APP-0021: 根据配置执行环境注册
        return FnVertx.combineT(
            // 第一个异步结果
            () -> this.context.registryAsync(this.container, config),
            // 第二个异步结果
            () -> Objects.isNull(this.extension) ?
                Future.succeededFuture(Set.of()) :
                this.extension.registryAsync(this.container, config),
            // 将构造好的 HArk 合并到一起
            this::registryOut
        );
    }

    // ------------------------ 私有部分 -----------------------
    private Future<Set<HArk>> registryOut(final Set<HArk> source, final Set<HArk> extension) {
        final Set<HArk> combine = combine(source, extension);
        combine.forEach(RUNNING::registry);
        return Future.succeededFuture(combine);
    }
}
