package io.zerows.epoch.corpus.metadata.service.context;

import io.zerows.epoch.sdk.metadata.service.ServiceContext;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-07-01
 */
public class ContextOfPlugin implements ServiceContext {

    private final Bundle owner;
    private final DataContext context;

    public ContextOfPlugin(final Bundle owner) {
        this.owner = owner;
        this.context = DataContext.of(owner);
    }

    @Override
    public Bundle owner() {
        return this.owner;
    }

    @Override
    public ServiceContext put(final String field, final Object value) {
        if (Objects.nonNull(value)) {
            this.context.put(field, value);
        }
        return this;
    }

    @Override
    public ServiceContext putOr(final String field, final Object value) {
        if (Objects.nonNull(value)) {
            this.context.put(field, value);

            DataContext.putGlobal(field, value);
        }
        return this;
    }

    @Override
    public ServiceContext remove(final String field) {
        return Objects.requireNonNull(this.context).get(field);
    }

    @Override
    public <T> T get(final String field) {
        return this.context.get(field);
    }

    @Override
    public <T> T getOr(final String field) {
        T valueRef = this.context.get(field);
        if (Objects.isNull(valueRef)) {
            valueRef = DataContext.getGlobal(field);
        }
        return valueRef;
    }
}
