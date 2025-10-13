package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class InquirerClassActor implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} Actor ) \uD83E\uDDEC Zero 中扫描到 {} 个 @Actor 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> allClasses) {
        final Set<Class<?>> actorSet = new HashSet<>();
        allClasses.stream()
            .filter(clazz -> clazz.isAnnotationPresent(Actor.class))
            .forEach(actorSet::add);

        // Ensure Actor Part
        if (!actorSet.isEmpty()) {
            log.info(MESSAGE, actorSet.size(), actorSet.size());
        }
        return actorSet;
    }
}
