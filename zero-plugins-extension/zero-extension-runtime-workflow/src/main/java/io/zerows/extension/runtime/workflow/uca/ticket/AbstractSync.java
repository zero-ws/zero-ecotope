package io.zerows.extension.runtime.workflow.uca.ticket;

import io.zerows.extension.runtime.workflow.atom.configuration.MetaInstance;

public abstract class AbstractSync implements Sync {
    protected final transient MetaInstance metadata;

    public AbstractSync(final MetaInstance metadata) {
        this.metadata = metadata;
    }
}
