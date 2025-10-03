package io.zerows.weaver;

import io.zerows.epoch.metadata.KView;

class SaberVis extends SaberBase {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return KView.smart(literal);
    }
}
