package io.zerows.epoch.corpus.model.uca.scan.parallel;

import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.atom.Event;
import io.zerows.epoch.corpus.model.uca.extract.Extractor;
import io.zerows.epoch.corpus.model.uca.extract.ExtractorEvent;

import java.util.HashSet;
import java.util.Set;

public class EndPointThread extends Thread {

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
            LOGGER.info(INFO.SCANNED_EVENTS, this.reference.getName(),
                this.events.size());
        }
    }

    public Set<Event> getEvents() {
        return this.events;
    }
}
