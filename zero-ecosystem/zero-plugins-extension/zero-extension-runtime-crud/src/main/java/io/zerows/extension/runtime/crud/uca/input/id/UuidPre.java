package io.zerows.extension.runtime.crud.uca.input.id;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.mbse.atom.specification.KModule;
import io.zerows.epoch.metadata.specification.KField;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;

import java.util.UUID;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UuidPre implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final KModule module = in.module();
        // 主键生成
        this.generateKey(data, module);
        return Ux.future(data);
    }

    private void generateKey(final JsonObject data, final KModule module) {
        /* 提取当前模型主键 */
        final KField field = module.getField();
        final String keyField = field.getKey();

        /* 无主键跳出 */
        if (Ut.isNil(keyField)) {
            return;
        }

        /* 抽取主键值，若无值则设置 */
        final String keyValue = data.getString(keyField);
        if (Ut.isNil(keyValue)) {
            /* UUID 主键 */
            data.put(keyField, UUID.randomUUID().toString());
        }
    }

    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        final KModule module = in.module();
        Ut.itJArray(data).forEach(json -> this.generateKey(json, module));
        return Ux.future(data);
    }
}
