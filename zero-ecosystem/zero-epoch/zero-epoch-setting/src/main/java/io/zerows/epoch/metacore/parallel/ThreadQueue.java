package io.zerows.epoch.metacore.parallel;

import io.r2mo.function.Actuator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ThreadQueue {

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
            log.error("[ ZERO ] 扫描线程 ThreadQueue 中断", ex);
        }
    }

    private void startAsync() {
        for (final Thread thread : this.threads) {
            thread.start();
        }
    }
}
