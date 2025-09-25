package io.zerows.module.configuration.zdk;

import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;

public interface Transformer<T> {
    T transform(JsonObject input);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}
