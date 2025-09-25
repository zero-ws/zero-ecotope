package io.zerows.module.assembly.uca.scan;


import io.zerows.core.annotations.Agent;
import io.zerows.module.metadata.zdk.uca.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerMAgent implements Inquirer<Set<Class<?>>> {

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Agent.class))
            .collect(Collectors.toSet());
        if (!agents.isEmpty()) {
            this.logger().info(INFO.AGENT, agents.size());
        }
        return agents;
    }
}
