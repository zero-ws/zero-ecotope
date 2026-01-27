package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.exception._40049Exception500WorkerConflict;
import io.zerows.epoch.jigsaw.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is For annotation @Queue scanning
 * It will scan all classes that annotated with @Queue, zero system
 * will extract worker class from this scanned classes.
 */
@Slf4j
public class InquirerClassQueue implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} Queue ) \uD83E\uDDEC Zero 中扫描到 {} 个 @Queue 组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> queues = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Queue.class))
            .collect(Collectors.toSet());
        if (!queues.isEmpty()) {
            log.info(MESSAGE, queues.size(), queues.size());
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
                // Fix: Exception in thread "meditate-class-2" java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
                final Class<?>[] parameterTypes = method.getParameterTypes();
                if (0 == parameterTypes.length) {
                    return;
                }
                final Class<?> returnType = method.getReturnType();
                final Class<?> parameterType = parameterTypes[0];
                if (Message.class.isAssignableFrom(parameterType)) {
                    Fn.jvmKo(void.class != returnType && Void.class != returnType, _40049Exception500WorkerConflict.class, method);
                }
            });
    }
}
