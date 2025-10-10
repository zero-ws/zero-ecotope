package io.zerows.epoch.assembly.parallel;

import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.assembly.ExtractorEvent;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.support.Ut;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EndPointThread extends Thread {

    public static final String SCANNED_EVENTS = "[ ZERO ] ( {} Event ) ---> @EndPoint 端对象 {} 包含 {} Events 定义！";

    @Getter
    private final Set<WebEvent> events = new HashSet<>();

    private final transient Extractor<Set<WebEvent>> extractor =
        Ut.instance(ExtractorEvent.class);

    private final transient Class<?> reference;

    public EndPointThread(final Class<?> clazz) {
        this.setName("zero-endpoint-scanner-" + this.threadId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.events.addAll(this.extractor.extract(this.reference));
            log.info(SCANNED_EVENTS, this.events.size(),
                this.reference.getName(), this.events.size());
        }
    }
}
