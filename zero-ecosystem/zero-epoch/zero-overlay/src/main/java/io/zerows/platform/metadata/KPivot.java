package io.zerows.platform.metadata;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.zerows.platform.exception._40104Exception409RegistryDuplicated;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.atomic.HBelong;
import io.zerows.specification.cloud.HFrontier;
import io.zerows.specification.cloud.HGalaxy;
import io.zerows.specification.cloud.HSpace;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.specification.vital.HOI;
import io.zerows.spi.HPI;
import io.zerows.support.base.UtBase;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
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
@SuppressWarnings("all")
public class KPivot<T> {
    /**
     * {@link HAmbient} 中存储了当前运行的上下文环境，该上下文环境中会包含对应的核心运行信息
     * 此信息可以替换原始架构中的主架构
     * <pre><code>
     *     废弃的部分
     *     1. Core,     KApp / KTenant
     *     2. zero-jet, Ambient / AmbientEnvironment / JtApp
     *     替换结构
     *     1. {@link HAmbient}
     *        key = {@link HArk}
     *               - {@link HApp}
     *               - {@link DBS}
     *                  - {@link Database}
     *               - {@link HBelong}
     *                  - {@link HFrontier}
     *                  - {@link HGalaxy}
     *                  - {@link HSpace}
     *     2. {@link HRegistry}
     *        当前运行的 {@link HAmbient} 为一个单独的实例，也运行在单节点中
     * </code></pre>
     */
    private static final HAmbient RUNNING = KAmbient.of();
    private final T container;
    private final HRegistry<T> context;

    private final HRegistry<T> extension;

    private KPivot(final T container) {
        this.container = container;
        this.context = new KRegistry<>();
        this.extension = HPI.findOneOf(HRegistry.class);
    }

    public static HAmbient running() {
        synchronized (KPivot.class) {
            return RUNNING;
        }
    }

    public static <T> KPivot<T> of(final T container) {
        return new KPivot<>(container);
    }

    private static void fail(final Class<?> clazz,
                             final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            throw new _40104Exception409RegistryDuplicated(stored.size());
        }
    }

    private static Future<Boolean> failAsync(final Class<?> clazz,
                                             final HAmbient ambient) {
        final ConcurrentMap<String, HArk> stored = ambient.app();
        if (!stored.isEmpty()) {
            return Future.failedFuture(new _40104Exception409RegistryDuplicated(stored.size()));
        } else {
            return Future.succeededFuture(Boolean.TRUE);
        }
    }

    private static Set<HArk> combine(final Set<HArk> sources, final Set<HArk> extensions) {
        sources.forEach(source -> {
            // 1. 先做租户过滤
            final HOI owner = source.owner();
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

    public Set<HArk> registry(final HConfig config) {
        // 前置检查（注册拦截）
        fail(getClass(), RUNNING);

        Set<HArk> contextDefault = this.context.registry(this.container, config);
        final Set<HArk> contextCombine = new HashSet<>();
        if (Objects.nonNull(this.extension)) {
            final Set<HArk> contextExtension = this.extension.registry(this.container, config);
            contextCombine.addAll(combine(contextDefault, contextExtension));
        }
        contextCombine.forEach(RUNNING::registry);
        return contextCombine;
    }

    public Future<Set<HArk>> registryAsync(final HConfig config) {
        // 纯容器模式
        if (Objects.isNull(config)) {
            return Future.succeededFuture(new HashSet<>());
        }
        // 前置检查（异步注册拦截）
        return failAsync(getClass(), RUNNING).compose(nil -> FnVertx.<Set<HArk>, Set<HArk>, Set<HArk>>combineT(
            // 第一个异步结果
            () -> this.context.registryAsync(this.container, config),
            // 第二个异步结果
            () -> this.registryExtension(config),
            // 合并函数
            this::registryOut
        ));
    }

    // ------------------------ 私有部分 -----------------------
    private Future<Set<HArk>> registryOut(final Set<HArk> source, final Set<HArk> extension) {
        final Set<HArk> combine = combine(source, extension);
        combine.forEach(RUNNING::registry);
        return Future.succeededFuture(combine);
    }

    private Future<Set<HArk>> registryExtension(final HConfig config) {
        if (Objects.isNull(this.extension)) {
            return Future.succeededFuture();
        } else {
            return this.extension.registryAsync(this.container, config);
        }
    }
}
