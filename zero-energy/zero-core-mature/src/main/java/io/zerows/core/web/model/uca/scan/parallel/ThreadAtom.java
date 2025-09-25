package io.zerows.core.web.model.uca.scan.parallel;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;

import java.util.concurrent.CountDownLatch;

public class ThreadAtom extends Thread {

    private final transient CountDownLatch counter;
    private final transient Actuator consumer;

    ThreadAtom(final CountDownLatch counter,
               final Actuator consumer) {
        this.counter = counter;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Fn.jvmAt(this.consumer);
        this.counter.countDown();
    }
}
