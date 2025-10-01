package io.zerows.epoch.corpus.configuration.zdk;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

public interface Transformer<T> {
    T transform(JsonObject input);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}
