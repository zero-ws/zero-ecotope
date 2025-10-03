package io.zerows.component.scanner;


import io.zerows.epoch.annotations.Worker;
import io.zerows.sdk.environment.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerMWorker implements Inquirer<Set<Class<?>>> {

    public static final String WORKER = "( Worker ) The Zero system has found {0} components of @Worker.";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> workers = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Worker.class))
            .collect(Collectors.toSet());
        if (!workers.isEmpty()) {
            this.logger().info(WORKER, workers.size());
        }
        return workers;
    }
}
