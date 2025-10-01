package io.zerows.epoch.bootplus.extension.migration.modeling;

import io.zerows.extension.mbse.basement.domain.tables.pojos.MField;

public class FieldRevision extends AbstractRevision {

    public FieldRevision() {
        super(MField.class);
    }
}
