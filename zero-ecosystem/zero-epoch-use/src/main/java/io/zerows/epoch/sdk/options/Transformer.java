package io.zerows.epoch.sdk.options;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.program.Ut;

public interface Transformer<T> {
    T transform(JsonObject input);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}
