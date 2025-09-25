package io.zerows.extension.mbse.basement.osgi.spi.plugin;

import io.zerows.common.datamation.KFabric;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;

@SuppressWarnings("unchecked")
public interface DataPlugin<T> {

    default T bind(final DataAtom atom) {
        return (T) this;
    }

    default T bind(final KFabric fabric) {
        return (T) this;
    }
}
