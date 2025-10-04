package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.Agent;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.sdk.environment.Inquirer;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * This component is for @Agent annotated class
 */
public class InquirerForAgent implements
    Inquirer<ConcurrentMap<ServerType, List<Class<?>>>> {

    @Override
    public ConcurrentMap<ServerType, List<Class<?>>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents =
            classes.stream()
                .filter((item) -> item.isAnnotationPresent(Agent.class))
                .collect(Collectors.toSet());
        return Ut.elementGroup(agents, this::getAgentKey,
            (item) -> item);
    }

    public ServerType getAgentKey(final Class<?> clazz) {
        if (clazz.isAnnotationPresent(Agent.class)) {
            return clazz.getDeclaredAnnotation(Agent.class).type();
        }
        return null;
    }
}
