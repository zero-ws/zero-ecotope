package io.zerows.component.extract;

import io.zerows.component.log.OLog;
import io.zerows.support.Ut;

public interface Extractor<T> {

    T extract(Class<?> clazz);

    default OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
