package io.zerows.epoch.component.scanner;


import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.sdk.metadata.uca.Inquirer;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerIpc implements Inquirer<Set<Class<?>>> {

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
            .filter((item) -> {
                final Agent anno = item.getAnnotation(Agent.class);
                if (Objects.isNull(anno)) {
                    return false;
                }
                final ServerType serverType = anno.type();
                return ServerType.IPC == serverType;
            })
            .collect(Collectors.toSet());
        if (!agents.isEmpty()) {
            this.logger().info(INFO.RPC, agents.size());
        }
        return agents;
    }
}
