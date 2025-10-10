package io.zerows.epoch.assembly;


import io.zerows.epoch.annotations.Worker;
import io.zerows.epoch.configuration.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class InquirerClassWorker implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} Worker ) \uD83E\uDDEC Zero 中扫描到 {} 个 @Worker 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> workers = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Worker.class))
            .collect(Collectors.toSet());
        if (!workers.isEmpty()) {
            log.info(MESSAGE, workers.size(), workers.size());
        }
        return workers;
    }
}
