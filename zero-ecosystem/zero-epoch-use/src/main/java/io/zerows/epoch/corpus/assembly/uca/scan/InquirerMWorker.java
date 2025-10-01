package io.zerows.epoch.corpus.assembly.uca.scan;


import io.zerows.epoch.annotations.Worker;
import io.zerows.epoch.corpus.metadata.zdk.uca.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class InquirerMWorker implements Inquirer<Set<Class<?>>> {

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> workers = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Worker.class))
            .collect(Collectors.toSet());
        if (!workers.isEmpty()) {
            this.logger().info(INFO.WORKER, workers.size());
        }
        return workers;
    }
}
