package io.zerows.extension.mbse.basement.uca.id;

import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.specification.modeling.HRecord;

class JoinCollectionId extends AbstractId {

    @Override
    public <ID> ID key(final HRecord record,
                       final Model model) {

        return null;
    }

    @Override
    public <ID> void key(final HRecord record,
                         final Model model,
                         final ID id) {

    }
}
