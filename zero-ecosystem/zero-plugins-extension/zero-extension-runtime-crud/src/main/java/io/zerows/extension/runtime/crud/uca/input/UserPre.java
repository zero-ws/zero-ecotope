package io.zerows.extension.runtime.crud.uca.input;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.unity.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UserPre implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Envelop envelop = in.envelop();
        data.put(KName.USER, envelop.userId());
        data.put(KName.HABITUS, envelop.habitus());
        return Ux.future(data);
    }
}
