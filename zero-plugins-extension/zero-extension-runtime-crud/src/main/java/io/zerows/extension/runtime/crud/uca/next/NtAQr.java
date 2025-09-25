package io.zerows.extension.runtime.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.eon.Pooled;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
class NtAQr implements Co<JsonObject, JsonArray, JsonArray, JsonArray> {
    private transient final IxMod in;
    private transient final Co<JsonArray, JsonArray, JsonArray, JsonArray> record;

    NtAQr(final IxMod in) {
        this.in = in;
        this.record = Pooled.CCT_CO.pick(() -> new NtAData(in), NtAData.class.getName() + in.cached());
        // Fx.po?lThread(Pooled.CO_MAP, () -> new NtAData(in), NtAData.class.getName() + in.keyPool());
    }

    @Override
    public Future<JsonArray> next(final JsonObject input, final JsonArray active) {
        return this.record.next(new JsonArray(), active);
    }

    @Override
    public Future<JsonArray> ok(JsonArray active, JsonArray standBy) {
        return this.record.next(active, standBy);
    }
}
