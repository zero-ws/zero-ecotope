package io.zerows.epoch.bootplus.extension.migration.modeling;

import io.zerows.extension.mbse.basement.domain.tables.pojos.MEntity;

public class EntityRevision extends AbstractRevision {

    public EntityRevision() {
        super(MEntity.class);
    }
}
