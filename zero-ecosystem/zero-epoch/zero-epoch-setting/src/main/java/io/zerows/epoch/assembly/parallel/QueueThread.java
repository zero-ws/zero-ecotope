package io.zerows.epoch.assembly.parallel;

import io.zerows.epoch.assembly.Extractor;
import io.zerows.epoch.assembly.ExtractorReceipt;
import io.zerows.epoch.basicore.WebReceipt;
import io.zerows.support.Ut;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class QueueThread extends Thread {

    public static final String SCANNED_RECEIPTS = "[ ZERO ] ( {} Receipt ) <--- @Queue 队列对象 {} 包含了 {} Receipt 定义！ ";

    @Getter
    private final Set<WebReceipt> receipts = new HashSet<>();

    private final transient Extractor<Set<WebReceipt>> extractor =
        Ut.instance(ExtractorReceipt.class);

    private final transient Class<?> reference;

    public QueueThread(final Class<?> clazz) {
        this.setName("zero-queue-scanner-" + this.threadId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.receipts.addAll(this.extractor.extract(this.reference));
            log.info(SCANNED_RECEIPTS, this.receipts.size(),
                this.reference.getName(), this.receipts.size());
        }
    }
}
