package io.zerows.extension.module.workflow.component.ticket;

import io.zerows.extension.module.workflow.metadata.MetaInstance;

public abstract class AbstractSync implements Sync {
    protected final transient MetaInstance metadata;

    public AbstractSync(final MetaInstance metadata) {
        this.metadata = metadata;
    }
}
