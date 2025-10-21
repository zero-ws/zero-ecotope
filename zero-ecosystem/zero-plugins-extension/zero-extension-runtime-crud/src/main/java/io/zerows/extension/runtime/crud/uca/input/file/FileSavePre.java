package io.zerows.extension.runtime.crud.uca.input.file;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.skeleton.spi.ExAttachment;

/**
 * 附件同步
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class FileSavePre extends FileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> Ux.channel(
            ExAttachment.class,                                       // Component
            JsonArray::new,                                         // JsonArray Data
            file -> file.saveAsync(criteria, dataArray, data)       // Execution Logical
        )).apply(data);
    }
}
