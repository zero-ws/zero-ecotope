package io.zerows.extension.runtime.crud.uca.input.file;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Attachment;
import io.zerows.unity.Ux;

/**
 * 附件上传
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class FileUploadPre extends FileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> Ux.channel(
            Attachment.class,                           // Component
            JsonArray::new,                             // JsonArray Data
            file -> file.uploadAsync(dataArray, data)   // Execution Logical
        )).apply(data);
    }
}
