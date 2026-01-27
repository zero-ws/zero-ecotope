package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 极致性能版 V2 (恢复并行分发)
 *
 * @author lang : 2025-10-13
 */
@Slf4j
public class ZeroModule<T> {

    private static final Cc<String, ZeroModule<?>> CC_MODULE = Cc.openThread();

    // 静态懒加载矩阵
    private static final ConcurrentMap<Integer, Set<HActor>> ACTOR_MATRIX = new ConcurrentHashMap<>();
    private static final List<Integer> ACTOR_SEQUENCE = new ArrayList<>();
    private static final Object LOCK = new Object();
    // DCL 锁
    private static volatile boolean INITIALIZED = false;
    private final T container;

    private ZeroModule(final T container) {
        this.container = container;
    }

    @SuppressWarnings("unchecked")
    public static <T> ZeroModule<T> of(final T container) {
        final String cacheKey = container.getClass().getName() + ":" + System.identityHashCode(container);
        return (ZeroModule<T>) CC_MODULE.pick(() -> new ZeroModule<>(container), cacheKey);
    }

    private static void ensureInitialized() {
        if (INITIALIZED) {
            return;
        }
        synchronized (LOCK) {
            if (INITIALIZED) {
                return;
            }
            try {
                initGlobalMatrix();
            } finally {
                INITIALIZED = true;
            }
        }
    }

    private static void initGlobalMatrix() {
        final Set<Class<?>> clazzSet = OCacheClass.entireValue();
        log.info("[ PLUG ] 扫描起点类数量：{}", clazzSet.size());

        if (clazzSet.isEmpty()) {
            return;
        }

        // 使用并行流加速类扫描和实例化 (这是 CPU 密集型操作)
        // 注意：这里只是构建列表，不涉及 Vert.x Context，所以 parallelStream 是安全的且极快
        final List<HActor> actors = clazzSet.parallelStream()
            .filter(each -> Ut.isImplement(each, HActor.class))
            .filter(each -> each.isAnnotationPresent(Actor.class))
            .map(each -> (HActor) Ut.singleton(each))
            .toList();

        for (final HActor actor : actors) {
            final Actor annotation = actor.getClass().getDeclaredAnnotation(Actor.class);
            ACTOR_MATRIX.computeIfAbsent(annotation.sequence(), k -> ConcurrentHashMap.newKeySet()).add(actor);
        }

        ACTOR_SEQUENCE.addAll(ACTOR_MATRIX.keySet().stream()
            .sorted(Comparator.naturalOrder())
            .toList());
    }

    // ---------------------- DCL 初始化 ----------------------

    public Future<Boolean> startActor(final Predicate<Integer> sequenceFn) {
        ensureInitialized();
        return this.runActor(sequenceFn, "启动", (config, actor) -> actor.startAsync(config, this.container));
    }

    public Future<Boolean> stopAsync(final Predicate<Integer> sequenceFn) {
        if (!INITIALIZED) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        return this.runActor(sequenceFn, "停止", (config, actor) -> actor.stopAsync(config, this.container));
    }

    // ---------------------- 执行引擎 (并行恢复版) ----------------------

    private Future<Boolean> runActor(final Predicate<Integer> sequenceFn,
                                     final String phase,
                                     final BiFunction<HConfig, HActor, Future<Boolean>> executorFn) {
        Future<Boolean> future = Future.succeededFuture(Boolean.TRUE);

        for (final Integer sequence : ACTOR_SEQUENCE) {
            if (!sequenceFn.test(sequence)) {
                continue;
            }

            final Set<HActor> actorSet = ACTOR_MATRIX.get(sequence);
            if (Objects.isNull(actorSet) || actorSet.isEmpty()) {
                future = future.compose(nil -> Future.succeededFuture(Boolean.TRUE));
            } else {
                future = future.compose(nil -> {
                    log.info("[ PLUG ] \uD83E\uDDCA ---> {} sequence = `{}` 的 Actor 集合，共 {} 个组件",
                        phase, String.format("%6d", sequence), actorSet.size());

                    /*
                     * ⚡⚡ 核心修复：恢复 parallelStream ⚡⚡
                     * 原因：findConfig 和 反射读取注解是 CPU 操作，串行执行会严重拖慢启动速度。
                     * 我们利用 parallelStream 并行地去“准备”和“触发”任务，
                     * 然后收集回来的 List<Future> 交给 Vert.x 去 join。
                     * * 这样既享受了多核 CPU 的准备速度，又享受了 Vert.x 的异步等待。
                     */
                    final List<Future<?>> futures = actorSet.parallelStream()
                        .map(actor -> {
                            // 1. (并行) 耗时的配置查找和反射
                            final Actor actorAnnotation = actor.getClass().getDeclaredAnnotation(Actor.class);
                            final HConfig config = this.findConfig(actor, actorAnnotation);

                            // 2. (并行) 校验逻辑
                            if (actorAnnotation.configured() && Objects.isNull(config)) {
                                // 跳过应该是 info 而不是 warn
                                log.info("[ PLUG ]    ⚪️ ---> 跳过 actor = `{}`, 检查配置项：`{}`",
                                    actor.getClass().getName(), actorAnnotation.value());
                                return null;
                            }

                            // 3. (并行) 触发启动。注意：startAsync 内部通常是非阻塞的，
                            // 但如果 startAsync 前半部分有同步代码，这里并行执行收益巨大。
                            return executorFn.apply(config, actor);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                    if (futures.isEmpty()) {
                        return Future.succeededFuture(Boolean.TRUE);
                    }

                    // Vert.x 5 Future.join 等待所有并行任务完成
                    return Future.join(futures)
                        .map(Boolean.TRUE)
                        .otherwise(error -> {
                            log.error("[ PLUG ] 执行异常 --> ", error);
                            return Boolean.FALSE;
                        });
                });
            }
        }
        return future;
    }

    // 提取为独立方法，方便 lambda 调用，同时减少 lambda 捕获变量
    private HConfig findConfig(final HActor actor, final Actor annotation) {
        final String configKey = annotation.value();
        HConfig config = NodeStore.findInfix(this.container, configKey);
        if (Objects.isNull(config)) {
            config = NodeStore.findExtension(this.container, configKey);
        }
        return config;
    }
}