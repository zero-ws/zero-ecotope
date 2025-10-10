package io.zerows.epoch.assembly;


import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.configuration.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class InquirerClassAgent implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} Agent ) \uD83E\uDDEC Zero 中扫描到 {} 个 @Agent 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Agent.class))
            .collect(Collectors.toSet());
        if (!agents.isEmpty()) {
            log.info(MESSAGE, agents.size(), agents.size());
        }
        return agents;
    }
}
