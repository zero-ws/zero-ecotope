package io.zerows.epoch.component.scan;

import io.r2mo.function.Fn;
import io.zerows.epoch.component.scan.parallel.EndPointThread;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.sdk.environment.Inquirer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class InquirerEvent implements Inquirer<Set<Event>> {


    @Override
    public Set<Event> scan(final Set<Class<?>> endpoints) {
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
        final Set<Event> events = new HashSet<>();
        Fn.jvmAt(() -> {
            for (final EndPointThread item : threadReference) {
                events.addAll(item.getEvents());
            }
        });
        return events;
    }
}
