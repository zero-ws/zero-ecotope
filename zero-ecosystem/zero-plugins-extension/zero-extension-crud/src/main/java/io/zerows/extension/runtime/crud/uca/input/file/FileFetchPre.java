package io.zerows.extension.runtime.crud.uca.input.file;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.skeleton.spi.ExAttachment;
import io.zerows.program.Ux;

/**
 * 附件读取
 *
 * @author lang : 2023-08-04
 */
class FileFetchPre extends FileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> Ux.channel(
            ExAttachment.class,                   // Component
            JsonArray::new,                     // JsonArray Data
            file -> file.fetchAsync(criteria)   // Execution Logical
        )).apply(data);
    }
}
