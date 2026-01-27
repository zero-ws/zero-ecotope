package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.jigsaw.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class InquirerClassEndPoint implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} EndPoint ) \uD83E\uDDEC Zero 中扫描到 {} 个 @EndPoint 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> endpoints = clazzes.stream()
            .filter((item) -> item.isAnnotationPresent(EndPoint.class))
            .collect(Collectors.toSet());
        if (!endpoints.isEmpty()) {
            log.info(MESSAGE, endpoints.size(), endpoints.size());
        }
        return endpoints;
    }
}
