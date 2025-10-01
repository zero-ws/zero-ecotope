package io.zerows.extension.runtime.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.domain.uca.destine.Conflate;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class NtAData implements Co<JsonArray, JsonArray, JsonArray, JsonArray> {

    private transient final IxMod in;

    NtAData(final IxMod in) {
        this.in = in;
    }

    @Override
    public Future<JsonArray> next(final JsonArray input, final JsonArray active) {
        if (this.in.canJoin()) {
            final Conflate<JsonArray, JsonArray> conflate =
                Conflate.ofJArray(this.in.connect(), false);

            final JsonArray dataSt = conflate.treat(active, input, this.in.connectId());
            {
                // 移除不必要的 `key` 属性
                final String key = this.in.module().getField().getKey();
                Ut.itJArray(dataSt).forEach(json -> json.remove(key));
            }
            LOG.Web.info(this.getClass(), "Data In: {0}", dataSt.encode());
            return Ux.future(dataSt);
        } else {
            // There is no joined module on current
            return Ux.future(active);
        }
    }

    @Override
    public Future<JsonArray> ok(final JsonArray active, final JsonArray standBy) {
        if (this.in.canJoin()) {
            final Conflate<JsonArray, JsonArray> conflate =
                Conflate.ofJArray(this.in.connect(), true);
            return Ux.future(conflate.treat(active, standBy, this.in.connectId()));
        } else {
            // There is no joined module on current
            return Ux.future(active.copy());
        }
    }
}
