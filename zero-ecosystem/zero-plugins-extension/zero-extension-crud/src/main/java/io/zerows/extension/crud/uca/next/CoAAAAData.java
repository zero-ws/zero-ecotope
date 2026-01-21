package io.zerows.extension.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.component.destine.Conflate;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class CoAAAAData implements Co<JsonArray, JsonArray, JsonArray, JsonArray> {

    private transient final IxMod in;

    CoAAAAData(final IxMod in) {
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
            log.info("{} Data In / {}", IxConstant.K_PREFIX, dataSt.encode());
            return Ux.future(dataSt);
        } else {
            // There is no joined module join current
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
            // There is no joined module join current
            return Ux.future(active.copy());
        }
    }
}
