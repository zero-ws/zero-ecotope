package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.exception._40049Exception500WorkerConflict;
import io.zerows.sdk.environment.Inquirer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is For annotation @Queue scanning
 * It will scan all classes that annotated with @Queue, zero system
 * will extract worker class from this scanned classes.
 */
public class InquirerClassQueue implements Inquirer<Set<Class<?>>> {

    public static final String QUEUE = "( {0} Queue ) The Zero system has found {0} components of @Queue.";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> queues = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Queue.class))
            .collect(Collectors.toSet());
        if (!queues.isEmpty()) {
            this.logger().info(QUEUE, queues.size());
            this.ensure(queues);
        }
        return queues;
    }

    private void ensure(final Set<Class<?>> clazzes) {
        clazzes.stream()
            .map(Class::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .filter(method -> method.isAnnotationPresent(Address.class))
            .forEach(method -> {
                final Class<?> returnType = method.getReturnType();
                final Class<?> parameterTypes = method.getParameterTypes()[0];
                if (Message.class.isAssignableFrom(parameterTypes)) {
                    Fn.jvmKo(void.class != returnType && Void.class != returnType, _40049Exception500WorkerConflict.class, method);
                }
            });
    }
}
