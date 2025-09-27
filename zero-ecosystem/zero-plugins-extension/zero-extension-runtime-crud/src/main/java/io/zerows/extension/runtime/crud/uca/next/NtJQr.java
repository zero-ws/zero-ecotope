package io.zerows.extension.runtime.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.module.domain.uca.destine.Conflate;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
class NtJQr implements Co<JsonObject, JsonObject, JsonObject, JsonObject> {
    private transient final IxMod in;
    private transient final Co<JsonObject, JsonObject, JsonObject, JsonObject> record;

    NtJQr(final IxMod in) {
        this.in = in;
        this.record = Pooled.CCT_CO.pick(() -> new NtJData(in), NtJData.class.getName() + in.cached());
        // RFn.po?lThread(Pooled.CO_MAP, () -> new NtJData(in), NtJData.class.getName() + in.keyPool());
    }

    @Override
    public Future<JsonObject> next(final JsonObject input, final JsonObject active) {
        if (this.in.canJoin()) {
            final Conflate<JsonObject, JsonObject> conflate =
                Conflate.ofQr(this.in.connect(), false);
            final JsonObject params = conflate.treat(active, this.in.connectId());
            return Ux.future(params);
        } else {
            return Ux.future(active.copy());
        }
    }

    @Override
    public Future<JsonObject> ok(JsonObject active, JsonObject standBy) {
        if (this.in.canJoin()) {
            return this.record.ok(active, standBy);
        } else {
            return Ux.future(active.copy());
        }
    }
}
