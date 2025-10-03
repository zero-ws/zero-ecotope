package io.zerows.component.scanner;


import io.zerows.epoch.annotations.Agent;
import io.zerows.sdk.environment.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerMAgent implements Inquirer<Set<Class<?>>> {

    public static final String AGENT = "( Agent ) The Zero system has found {0} components of @Agent.";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Agent.class))
            .collect(Collectors.toSet());
        if (!agents.isEmpty()) {
            this.logger().info(AGENT, agents.size());
        }
        return agents;
    }
}
