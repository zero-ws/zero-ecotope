package io.zerows.extension.runtime.skeleton.osgi.spi.ui;

import io.zerows.component.log.LogOf;
import io.zerows.epoch.database.jooq.operation.DBJooq;

/*
 * All sub-class for extension of Jooq type
 */
public abstract class Anchoret<T> {

    private transient DBJooq jooq;

    /*
     * This method is for sub-class only
     */
    @SuppressWarnings("unchecked")
    public T on(final DBJooq jooq) {
        this.jooq = jooq;
        return (T) this;
    }

    protected DBJooq dao() {
        return this.jooq;
    }

    protected LogOf getLogger() {
        return LogOf.get(this.getClass());
    }
}
