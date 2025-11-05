package io.zerows.extension.crud.uca.input;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件删除
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class PreFileRemovePre extends PreFileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> Ux.channel(
            ExAttachment.class,                                   // Component
            JsonArray::new,                                     // JsonArray Data
            file -> file.removeAsync(criteria)                  // Execution Logical
        )).apply(data);
    }

    /*
     * Batch Deleted
     */
    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        final List<Future<JsonObject>> futures = new ArrayList<>();
        Ut.itJArray(data).forEach(json -> futures.add(this.inJAsync(json, in)));
        return Fx.combineA(futures);
    }
}
