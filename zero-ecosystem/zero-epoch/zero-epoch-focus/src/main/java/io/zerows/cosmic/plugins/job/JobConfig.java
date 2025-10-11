package io.zerows.cosmic.plugins.job;

import io.zerows.epoch.metadata.MMComponent;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/*
 * Job configuration in `vertx-job.yml`, the job node
 * job:
 * - store:
 *   - component:
 *   - config:
 * - interval:
 *   - component:
 *   - config:
 * - client:
 *   - config:
 */
public class JobConfig implements Serializable {

    private transient MMComponent store;
    private transient MMComponent interval;
    private transient MMComponent client;

    public MMComponent getStore() {
        return Optional.ofNullable(this.store).orElse(new MMComponent());
    }

    public void setStore(final MMComponent store) {
        this.store = store;
    }

    public MMComponent getInterval() {
        final MMComponent componentOption = Optional.ofNullable(this.interval).orElse(new MMComponent());
        if (Objects.isNull(componentOption.getComponent())) {
            componentOption.setComponent(IntervalVertx.class);
        }
        return componentOption;
    }

    public void setInterval(final MMComponent interval) {
        this.interval = interval;
    }

    public MMComponent getClient() {
        return Optional.ofNullable(this.client).orElse(new MMComponent());
    }

    public void setClient(final MMComponent client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
            "get=" + this.store +
            ", interval=" + this.interval +
            ", client=" + this.client +
            '}';
    }
}
