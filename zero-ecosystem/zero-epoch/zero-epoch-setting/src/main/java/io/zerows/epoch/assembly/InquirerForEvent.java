package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.zerows.epoch.assembly.parallel.EndPointThread;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.configuration.Inquirer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class InquirerForEvent implements Inquirer<Set<ActorEvent>> {


    @Override
    public Set<ActorEvent> scan(final Set<Class<?>> endpoints) {
        final List<EndPointThread> threadReference = new ArrayList<>();
        /* 2.1.Build Api metadata **/
        for (final Class<?> endpoint : endpoints) {
            final EndPointThread thread =
                new EndPointThread(endpoint);
            threadReference.add(thread);
            thread.start();
        }
        /* 3.2. Join **/
        Fn.jvmAt(() -> {
            for (final EndPointThread item : threadReference) {
                item.join();
            }
        });
        /* 3.3. Finally **/
        final Set<ActorEvent> events = new HashSet<>();
        Fn.jvmAt(() -> {
            for (final EndPointThread item : threadReference) {
                events.addAll(item.getEvents());
            }
        });
        return events;
    }
}
