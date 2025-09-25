package io.zerows.core.web.model.uca.scan.parallel;

import io.r2mo.function.Actuator;
import io.zerows.core.uca.log.Annal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ThreadQueue {

    private static final Annal LOGGER = Annal.get(ThreadQueue.class);
    private final CountDownLatch counter;
    private final List<Thread> threads = new ArrayList<>();

    public ThreadQueue(final int size) {
        this.counter = new CountDownLatch(size);
    }

    public void add(final Actuator runnable,
                    final String name) {
        final Thread thread = new ThreadAtom(this.counter, runnable);
        thread.setName(name);
        this.threads.add(thread);
    }

    public void startSync() {
        this.startAsync();
        try {
            this.counter.await();
        } catch (final InterruptedException ex) {
            LOGGER.fatal(ex);
        }
    }

    private void startAsync() {
        for (final Thread thread : this.threads) {
            thread.start();
        }
    }
}
