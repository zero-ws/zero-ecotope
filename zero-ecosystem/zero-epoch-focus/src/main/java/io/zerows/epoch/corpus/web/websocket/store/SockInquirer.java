package io.zerows.epoch.corpus.web.websocket.store;

import io.r2mo.function.Fn;
import io.zerows.epoch.corpus.web.websocket.annotations.Subscribe;
import io.zerows.epoch.corpus.web.websocket.atom.Remind;
import io.zerows.epoch.corpus.web.websocket.eon.MessageOfSock;
import io.zerows.epoch.corpus.metadata.zdk.uca.Inquirer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockInquirer implements Inquirer<Set<Remind>> {

    @Override
    public Set<Remind> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> endpoints = clazzes.stream()
            .filter(this::isSocked)
            .collect(Collectors.toSet());
        this.logger().info(MessageOfSock.WEBSOCKET, endpoints.size());
        final List<SockThread> threadReference = new ArrayList<>();
        /* 2.1.Build Api metadata **/
        for (final Class<?> endpoint : endpoints) {
            final SockThread thread =
                new SockThread(endpoint);
            threadReference.add(thread);
            thread.start();
        }
        /* 3.2. Join **/
        Fn.jvmAt(() -> {
            for (final SockThread item : threadReference) {
                item.join();
            }
        });
        /* 3.3. Finally **/
        final Set<Remind> events = new HashSet<>();
        Fn.jvmAt(() -> {
            for (final SockThread item : threadReference) {
                events.addAll(item.getEvents());
            }
        });
        return events;
    }

    private boolean isSocked(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final long counter = Arrays.stream(methods)
            .filter(method -> method.isAnnotationPresent(Subscribe.class))
            .count();
        return 0 < counter;
    }
}
