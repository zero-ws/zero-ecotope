package io.mature.extension.migration.modeling;

import io.zerows.extension.mbse.basement.domain.tables.pojos.MModel;

public class ModelRevision extends AbstractRevision {

    public ModelRevision() {
        super(MModel.class);
    }
}
