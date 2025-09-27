package io.zerows.module.assembly.uca.scan;

import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.eventbus.Message;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.fn.RFn;
import io.zerows.module.assembly.exception.BootWorkerConflictException;
import io.zerows.module.metadata.zdk.uca.Inquirer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is For annotation @Queue scanning
 * It will scan all classes that annotated with @Queue, zero system
 * will extract worker class from this scanned classes.
 */
public class InquirerQueue implements Inquirer<Set<Class<?>>> {

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> queues = classes.stream()
            .filter((item) -> item.isAnnotationPresent(Queue.class))
            .collect(Collectors.toSet());
        if (!queues.isEmpty()) {
            this.logger().info(INFO.QUEUE, queues.size());
            this.ensure(queues);
        }
        return queues;
    }

    private void ensure(final Set<Class<?>> clazzes) {
        Observable.fromIterable(clazzes)
            .map(Class::getDeclaredMethods)
            .flatMap(Observable::fromArray)
            .filter(method -> method.isAnnotationPresent(Address.class))
            .subscribe(method -> {
                final Class<?> returnType = method.getReturnType();
                final Class<?> parameterTypes = method.getParameterTypes()[0];
                if (Message.class.isAssignableFrom(parameterTypes)) {
                    RFn.outBoot(void.class != returnType && Void.class != returnType, this.logger(),
                        BootWorkerConflictException.class, this.getClass(), method);
                }
            })
            .dispose();
    }
}
