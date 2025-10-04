package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.zerows.component.log.Annal;
import io.zerows.epoch.assembly.parallel.QueueThread;
import io.zerows.epoch.basicore.WebReceipt;
import io.zerows.epoch.configuration.Inquirer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Receipt annotation scan
 * This thread is for Receipt extraction
 */
public class InquirerForReceipt implements Inquirer<Set<WebReceipt>> {

    private static final Annal LOGGER = Annal.get(InquirerForReceipt.class);

    @Override
    public Set<WebReceipt> scan(final Set<Class<?>> queues) {
        final List<QueueThread> threadReference = new ArrayList<>();
        /* 3.1. Build KMetadata **/
        for (final Class<?> queue : queues) {
            final QueueThread thread =
                new QueueThread(queue);
            threadReference.add(thread);
            thread.start();
        }
        /* 3.2. Join **/
        Fn.jvmAt(() -> {
            for (final QueueThread item : threadReference) {
                item.join();
            }
        });
        /* 3.3. Return **/
        final Set<WebReceipt> receipts = new HashSet<>();
        Fn.jvmAt(() -> threadReference.stream()
            .map(QueueThread::getReceipts)
            .forEach(receipts::addAll));
        /* 3.4. New Receipts replaced with Aeon System ( Enabled ) */
        return receipts;
    }
}
