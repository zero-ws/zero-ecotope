package io.zerows.epoch.assembly.parallel;

import io.zerows.component.log.Annal;
import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.assembly.ExtractorReceipt;
import io.zerows.epoch.basicore.ActorReceipt;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Set;

public class QueueThread extends Thread {

    public static final String SCANNED_RECEIPTS = "( {1} Receipt ) The queue {0} scanned {1} records of Receipt, " +
        "will be mounted to event bus.";
    private static final Annal LOGGER = Annal.get(QueueThread.class);

    private final Set<ActorReceipt> receipts = new HashSet<>();

    private final transient Extractor<Set<ActorReceipt>> extractor =
        Ut.instance(ExtractorReceipt.class);

    private final transient Class<?> reference;

    public QueueThread(final Class<?> clazz) {
        this.setName("zero-queue-scanner-" + this.getId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.receipts.addAll(this.extractor.extract(this.reference));
            LOGGER.info(SCANNED_RECEIPTS, this.reference.getName(),
                this.receipts.size());
        }
    }

    public Set<ActorReceipt> getReceipts() {
        return this.receipts;
    }
}
