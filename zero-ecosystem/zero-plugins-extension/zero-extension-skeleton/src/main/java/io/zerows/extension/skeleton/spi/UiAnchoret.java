package io.zerows.extension.skeleton.spi;

import io.zerows.component.log.LogOf;
import io.zerows.epoch.store.jooq.ADB;

/*
 * All sub-class for extension of Jooq type
 */
public abstract class UiAnchoret<T> {

    private transient ADB jooq;

    /*
     * This method is for sub-class only
     */
    @SuppressWarnings("unchecked")
    public T on(final ADB jooq) {
        this.jooq = jooq;
        return (T) this;
    }

    protected ADB dao() {
        return this.jooq;
    }

    protected LogOf getLogger() {
        return LogOf.get(this.getClass());
    }
}
