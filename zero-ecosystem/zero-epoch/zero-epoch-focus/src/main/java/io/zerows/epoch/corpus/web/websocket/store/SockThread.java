package io.zerows.epoch.corpus.web.websocket.store;

import io.zerows.component.log.OLog;
import io.zerows.epoch.component.extract.Extractor;
import io.zerows.epoch.corpus.web.websocket.atom.Remind;
import io.zerows.epoch.program.Ut;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class SockThread extends Thread {
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
            LOGGER.info(INFO.SCANNED_SOCKS, this.reference.getName(),
                this.reminds.size());
        }
    }

    public Set<Remind> getEvents() {
        return this.reminds;
    }
}
