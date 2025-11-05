package io.zerows.extension.module.mbsecore.api;

import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.platform.metadata.KFabric;

@SuppressWarnings("unchecked")
public interface DataPlugin<T> {

    default T bind(final DataAtom atom) {
        return (T) this;
    }

    default T bind(final KFabric fabric) {
        return (T) this;
    }
}
