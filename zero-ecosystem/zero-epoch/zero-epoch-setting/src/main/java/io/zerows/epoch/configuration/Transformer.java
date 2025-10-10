package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.support.Ut;

@Deprecated
public interface Transformer<T> {
    T transform(JsonObject input);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}
