package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
import io.zerows.support.fn.Fx;
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
 * @author lang : 2025-10-13
 */
@Slf4j
public class ZeroModule<T> {
    private static final Cc<String, ZeroModule<?>> CC_MODULE = Cc.openThread();
    private static final ConcurrentMap<Integer, Set<HActor>> ACTOR_MATRIX = new ConcurrentHashMap<>();
    private static final List<Integer> ACTOR_SEQUENCE = new ArrayList<>();
    private final T container;

    private ZeroModule(final T container) {
        this.container = container;

        this.initMatrix();
    }

    private void initMatrix() {
        if (ACTOR_MATRIX.isEmpty()) {
            final List<HActor> actors = HPI.findMany(HActor.class);
            for (final HActor actor : actors) {
                final Actor annotation = actor.getClass().getDeclaredAnnotation(Actor.class);
                if (Objects.isNull(annotation)) {
                    log.warn("[ ZERO ] ( Actor ) 类 {} 未配置 @Actor 注解，无法纳入 Actor 矩阵中！",
                        actor.getClass().getName());
                    continue;
                }
                final int sequence = annotation.sequence();
                ACTOR_MATRIX.computeIfAbsent(sequence, k -> ConcurrentHashMap.newKeySet()).add(actor);
            }

            ACTOR_SEQUENCE.addAll(ACTOR_MATRIX.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .toList());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ZeroModule<T> of(final T container) {
        final String cacheKey = container.getClass().getName() + "@" + container.hashCode();
        return (ZeroModule<T>) CC_MODULE.pick(() -> new ZeroModule<>(container), cacheKey);
    }

    public Future<Boolean> startActor(final Predicate<Integer> sequenceFn) {
        return this.runActor(sequenceFn, (config, actor) -> actor.startAsync(config, this.container));
    }

    public Future<Boolean> stopAsync(final Predicate<Integer> sequenceFn) {
        return this.runActor(sequenceFn, (config, actor) -> actor.stopAsync(config, this.container));
    }

    private Future<Boolean> runActor(final Predicate<Integer> sequenceFn, final BiFunction<HConfig, HActor, Future<Boolean>> executorFn) {
        Future<Boolean> future = Future.succeededFuture(Boolean.TRUE);
        for (final Integer sequence : ACTOR_SEQUENCE) {
            /*
             * 条件不满足则直接直接 continue，切换到下一轮
             */
            if (!sequenceFn.test(sequence)) {
                continue;
            }


            /*
             * 并行执行，如果有多个则直接执行多个
             */
            final Set<HActor> actorSet = ACTOR_MATRIX.get(sequence);
            if (Objects.isNull(actorSet) || actorSet.isEmpty()) {
                future = future.compose(nil -> Future.succeededFuture(Boolean.TRUE));
            } else {
                log.info("[ ZMOD ] \t \uD83E\uDDCA ---> 执行 sequence = {} 的 Actor 集合，共 {} 个组件", sequence, actorSet.size());
                future = future.compose(nil -> Fx.combineB(actorSet.stream().map(actor -> {
                    final HConfig config = this.findConfig(actor);
                    return executorFn.apply(config, actor);
                }).filter(Objects::nonNull).collect(Collectors.toSet())).otherwise(error -> {
                    log.error("[ ZMOD ] 执行异常 --> ", error);
                    return Boolean.FALSE;
                }));
            }
        }
        return future;
    }

    private HConfig findConfig(final HActor actor) {
        final Actor annotation = actor.getClass().getDeclaredAnnotation(Actor.class);
        final String configKey = annotation.value();
        return NodeStore.findInfix(this.container, configKey);
    }
}
