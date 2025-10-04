package io.zerows.cosmic.plugins.websocket;

import io.zerows.component.log.OLog;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockThread extends Thread {
    public static final String SCANNED_SOCKS = "( {1} WebSocket ) The endpoint {0} scanned {1} websockets of Event, " +
        "will be mounted to event bus.";
    private static final OLog LOGGER = Ut.Log.uca(SockThread.class);
    private final Set<Remind> reminds = new HashSet<>();


    private final transient Extractor<Set<Remind>> extractor =
        Ut.instance(SockExtractor.class);

    private final transient Class<?> reference;

    public SockThread(final Class<?> clazz) {
        this.setName("zero-web-socket-scanner-" + this.getId());
        this.reference = clazz;
    }


    @Override
    public void run() {
        if (null != this.reference) {
            this.reminds.addAll(this.extractor.extract(this.reference));
            LOGGER.info(SCANNED_SOCKS, this.reference.getName(),
                this.reminds.size());
        }
    }

    public Set<Remind> getEvents() {
        return this.reminds;
    }
}
