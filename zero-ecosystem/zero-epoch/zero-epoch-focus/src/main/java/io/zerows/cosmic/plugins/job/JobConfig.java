package io.zerows.cosmic.plugins.job;

import io.zerows.epoch.metadata.MMComponent;
import lombok.Data;

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
@Data
public class JobConfig implements Serializable {

    private transient MMComponent store;
    private transient MMComponent interval;
    private transient MMComponent client;

    public MMComponent getStore() {
        return Optional.ofNullable(this.store).orElse(new MMComponent());
    }
    
    public MMComponent getInterval() {
        final MMComponent componentOption = Optional.ofNullable(this.interval).orElse(new MMComponent());
        if (Objects.isNull(componentOption.getComponent())) {
            componentOption.setComponent(IntervalVertx.class);
        }
        return componentOption;
    }

    public MMComponent getClient() {
        return Optional.ofNullable(this.client).orElse(new MMComponent());
    }

    @Override
    public String toString() {
        return "JobConfig{" +
            "store=" + this.store +
            ", interval=" + this.interval +
            ", client=" + this.client +
            '}';
    }
}
