package io.zerows.epoch.component.extract;

import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;

public interface Extractor<T> {

    T extract(Class<?> clazz);

    default OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
