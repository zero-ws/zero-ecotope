package io.zerows.epoch.assembly;

import io.zerows.component.log.LogO;
import io.zerows.support.Ut;

public interface Extractor<T> {

    T extract(Class<?> clazz);

    default LogO logger() {
        return Ut.Log.uca(this.getClass());
    }
}
