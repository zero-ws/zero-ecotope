package io.zerows.extension.crud.uca.input;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.spi.HPI;

/**
 * 附件同步
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class PreFileSavePre extends PreFileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> HPI.of(ExAttachment.class).waitAsync(
            file -> file.saveAsync(criteria, dataArray, data),
            JsonArray::new
        )).apply(data);
    }
}
