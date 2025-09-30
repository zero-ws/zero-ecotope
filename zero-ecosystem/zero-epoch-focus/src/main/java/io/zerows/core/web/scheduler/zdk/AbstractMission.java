package io.zerows.core.web.scheduler.zdk;

import io.vertx.core.Vertx;
import io.zerows.core.annotations.Contract;
import io.zerows.core.web.scheduler.uca.running.JobPin;
import io.zerows.core.web.scheduler.uca.running.JobStore;

public abstract class AbstractMission {
    private final transient JobStore store = JobPin.getStore();
    @Contract
    private transient Vertx vertx;

    protected Vertx vertx() {
        return this.vertx;
    }

    protected JobStore store() {
        return this.store;
    }
}
