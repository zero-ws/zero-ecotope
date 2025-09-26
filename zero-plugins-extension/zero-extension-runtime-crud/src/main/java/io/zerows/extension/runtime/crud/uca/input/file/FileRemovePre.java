package io.zerows.extension.runtime.crud.uca.input.file;

import io.zerows.core.fn.RFn;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Attachment;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件删除
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class FileRemovePre extends FileAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return this.actionFn(in, (criteria, dataArray) -> Ux.channel(
            Attachment.class,                                   // Component
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
        return RFn.combineA(futures);
    }
}
