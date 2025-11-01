package io.zerows.extension.mbse.basement.osgi.spi.plugins;

import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
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
