package io.zerows.epoch.assembly.parallel;

import io.zerows.component.log.Annal;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.assembly.ExtractorEvent;
import io.zerows.epoch.basicore.Event;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Set;

public class EndPointThread extends Thread {

    public static final String SCANNED_EVENTS = "( {1} Event ) The endpoint {0} scanned {1} events of Event, " +
        "will be mounted to routing system.";
    private static final Annal LOGGER = Annal.get(EndPointThread.class);

    private final Set<Event> events = new HashSet<>();

    private final transient Extractor<Set<Event>> extractor =
        Ut.instance(ExtractorEvent.class);

    private final transient Class<?> reference;

    public EndPointThread(final Class<?> clazz) {
        this.setName("zero-endpoint-scanner-" + this.getId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.events.addAll(this.extractor.extract(this.reference));
            LOGGER.info(SCANNED_EVENTS, this.reference.getName(),
                this.events.size());
        }
    }

    public Set<Event> getEvents() {
        return this.events;
    }
}
