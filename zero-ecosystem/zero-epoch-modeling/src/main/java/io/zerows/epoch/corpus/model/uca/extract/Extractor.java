package io.zerows.epoch.corpus.model.uca.extract;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

public interface Extractor<T> {

    T extract(Class<?> clazz);

    default OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
