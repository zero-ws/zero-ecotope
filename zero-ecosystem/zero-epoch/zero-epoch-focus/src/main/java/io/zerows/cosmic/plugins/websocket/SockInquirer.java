package io.zerows.cosmic.plugins.websocket;

import io.r2mo.function.Fn;
import io.zerows.epoch.annotations.Subscribe;
import io.zerows.epoch.configuration.Inquirer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockInquirer implements Inquirer<Set<Remind>> {

    public static final String WEBSOCKET = "( {0} WebSocket ) The Zero system has found " +
        "{0} components of @EndPoint.";

    @Override
    public Set<Remind> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> endpoints = clazzes.stream()
            .filter(this::isSocked)
            .collect(Collectors.toSet());
        this.logger().info(WEBSOCKET, endpoints.size());
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
