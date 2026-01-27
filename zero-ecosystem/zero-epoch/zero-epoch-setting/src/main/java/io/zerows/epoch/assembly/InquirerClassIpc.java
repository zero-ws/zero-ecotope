package io.zerows.epoch.assembly;


import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.jigsaw.Inquirer;
import io.zerows.platform.enums.EmWeb;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class InquirerClassIpc implements Inquirer<Set<Class<?>>> {

    public static final String RPC = "[ ZERO ] ( Rpc ) 系统查找到 {} 个 IPC 类型的 @Agent 组件！";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
            .filter((item) -> {
                final Agent anno = item.getAnnotation(Agent.class);
                if (Objects.isNull(anno)) {
                    return false;
                }
                final EmWeb.ServerType serverType = anno.type();
                return EmWeb.ServerType.IPC == serverType;
            })
            .collect(Collectors.toSet());
        if (!agents.isEmpty()) {
            log.info(RPC, agents.size());
        }
        return agents;
    }
}
