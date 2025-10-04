package io.zerows.epoch.assembly;


import io.zerows.epoch.annotations.Agent;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.sdk.environment.Inquirer;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerClassIpc implements Inquirer<Set<Class<?>>> {

    public static final String RPC = "( Rpc ) The Zero system has found {0} components of @Agent.";

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
            this.logger().info(RPC, agents.size());
        }
        return agents;
    }
}
