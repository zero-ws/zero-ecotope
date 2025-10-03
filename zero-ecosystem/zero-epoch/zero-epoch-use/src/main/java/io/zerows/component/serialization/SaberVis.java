package io.zerows.component.serialization;

import io.zerows.epoch.corpus.metadata.commune.Vis;

class SaberVis extends SaberBase {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return Vis.smart(literal);
    }
}
