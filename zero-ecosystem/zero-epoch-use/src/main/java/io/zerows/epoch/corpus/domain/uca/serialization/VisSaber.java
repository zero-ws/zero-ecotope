package io.zerows.epoch.corpus.domain.uca.serialization;

import io.zerows.epoch.corpus.domain.atom.commune.Vis;

class VisSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return Vis.smart(literal);
    }
}
