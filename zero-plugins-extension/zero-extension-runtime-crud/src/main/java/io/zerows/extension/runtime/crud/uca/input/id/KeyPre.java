package io.zerows.extension.runtime.crud.uca.input.id;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.module.domain.atom.specification.KField;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class KeyPre implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Envelop request = in.envelop();
        final KModule module = in.module();
        final KField field = module.getField();
        /* Primary Key Add */
        if (Ut.isNotNil(field.getKey())) {
            final String originalKey = data.getString(field.getKey());
            /*
             * null for set key
             */
            if (Ut.isNil(originalKey)) {
                final String keyValue = Ux.getString1(request);
                if (Ut.isNotNil(keyValue)) {
                    data.put(field.getKey(), keyValue);
                }
            }
            /*
             * If the key existing, do not set `key = uuid` format to input data to
             * avoid key overwrite
             * Fix bug: Could not update linker data here.
             */
        }
        return Ux.future(data);
    }
}
