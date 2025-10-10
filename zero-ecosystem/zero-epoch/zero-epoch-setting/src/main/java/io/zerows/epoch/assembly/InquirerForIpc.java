package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Ipc;
import io.zerows.epoch.assembly.exception._40043Exception500IpcTarget;
import io.zerows.epoch.assembly.exception._40044Exception500IpcReturn;
import io.zerows.epoch.assembly.exception._40045Exception500IpcDirection;
import io.zerows.epoch.assembly.exception._40046Exception500IpcArgument;
import io.zerows.epoch.assembly.exception._40048Exception500RpcAgentAbsence;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class is for @Ipc and @Address must be in @Queue class instead of other specification.
 */
@Slf4j
public class InquirerForIpc implements Inquirer<ConcurrentMap<String, Method>> {

    private static final String MESSAGE = "[ ZERO ] ( {} Ipc ) \uD83E\uDDEC Zero 中扫描到 {} 个 @Ipc 定义点。";

    /**
     * @param classes all classes must be annotated with @Queue
     *
     * @return scanned classes.
     */
    @Override
    public ConcurrentMap<String, Method> scan(final Set<Class<?>> classes) {
        /*
         * Here are some specification for IPC community
         * 1. As IPC server, must extract @Ipc ( from ) part and registred to Etcd
         * 2. This address is published and other nodes could visit current Micro service.
         */
        final ConcurrentMap<String, Method> addresses = new ConcurrentHashMap<>();
        Observable.fromIterable(classes)
            .flatMap(clazz -> Observable.fromArray(clazz.getDeclaredMethods()))
            .filter(method -> method.isAnnotationPresent(Ipc.class))
            .map(this::ensureTarget)
            .map(this::ensureSpec)
            .map(method -> this.ensureAgent(method, classes))
            .subscribe(method -> {
                final Ipc annotation = method.getAnnotation(Ipc.class);
                final String address = annotation.value();
                addresses.put(address, method);
            })
            .dispose();
        log.info(MESSAGE, addresses.size(), addresses.size());
        return addresses;
    }

    /**
     * Method with @Ipc must contain return type
     *
     * @param method method reference that scanned by zero
     *
     * @return Whether this method is valid
     */
    private Method ensureSpec(final Method method) {
        Fn.jvmKo(Ut.isVoid(method.getReturnType()), _40044Exception500IpcReturn.class, method);
        final Ipc annotation = method.getAnnotation(Ipc.class);
        final String value = annotation.value();
        if (!Ut.isNil(value)) {
            // TypedArgument specification: Non Start Node
            // This specification is only for continue node
            final Class<?>[] argTypes = method.getParameterTypes();
            Fn.jvmKo(1 != argTypes.length || Envelop.class != argTypes[0], _40046Exception500IpcArgument.class, method);
        }
        return method;
    }

    /**
     * If declaring class is interface, it must contains implementation classes.
     *
     * @param classes all classes that be sure to consider as agent
     *
     * @return valid method reference
     */
    private Method ensureAgent(final Method method, final Set<Class<?>> classes) {
        // Get declare clazz
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz.isAnnotationPresent(EndPoint.class)
            && clazz.isInterface()) {
            final Long counter = Observable.fromIterable(classes)
                .filter(item -> clazz != item)
                .filter(clazz::isAssignableFrom)
                .count().blockingGet();
            // If counter == 0, zero system disable this definition
            Fn.jvmKo(0 == counter, _40048Exception500RpcAgentAbsence.class, clazz);
        }
        return method;
    }

    /**
     * If set to or name, must not be null/empty at the sametime.
     *
     * @param method the method that should be checked here.
     *
     * @return valid method reference in the container
     */
    private Method ensureTarget(final Method method) {
        final Ipc annotation = method.getAnnotation(Ipc.class);
        final String to = Ut.invoke(annotation, "to");
        final String name = Ut.invoke(annotation, "name");
        if (Ut.isNil(to) && Ut.isNil(name)) {
            // If ( to is null and name is null, get must be required, or the system do not know the direction
            final String from = annotation.value();
            Fn.jvmKo(Ut.isNil(from), _40045Exception500IpcDirection.class, method);
            // Passed validation.
            return method;
        }
        // to and name must not be null
        Fn.jvmKo(Ut.isNil(to) || Ut.isNil(name), _40043Exception500IpcTarget.class, method, to, name);
        return method;
    }
}
