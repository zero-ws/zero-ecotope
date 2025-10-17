package io.zerows.cosmic.plugins.job;

import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Contract;

public abstract class AbstractMission {
    private final transient JobStore store = JobClientManager.of().getStore();
    @Contract
    private transient Vertx vertx;

    protected Vertx vertx() {
        return this.vertx;
    }

    protected JobStore store() {
        return this.store;
    }
}
