package io.zerows.core.web.model.uca.extract;

import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;

public interface Extractor<T> {

    T extract(Class<?> clazz);

    default OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
