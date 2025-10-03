package io.zerows.extension.runtime.skeleton.osgi.spi.ui;

import io.zerows.epoch.corpus.database.jooq.operation.UxJooq;
import io.zerows.component.log.Annal;

/*
 * All sub-class for extension of Jooq type
 */
public abstract class Anchoret<T> {

    private transient UxJooq jooq;

    /*
     * This method is for sub-class only
     */
    @SuppressWarnings("unchecked")
    public T on(final UxJooq jooq) {
        this.jooq = jooq;
        return (T) this;
    }

    protected UxJooq dao() {
        return this.jooq;
    }

    protected Annal getLogger() {
        return Annal.get(this.getClass());
    }
}
