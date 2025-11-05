package io.zerows.extension.crud.uca.input;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.crud.uca.desk.IxMod;
import io.zerows.program.Ux;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UriPre implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Envelop envelop = in.envelop();
        final User user = envelop.user();
        /* Null user */
        if (Objects.isNull(user)) {
            return Ux.future(data);
        }
        final JsonObject principle = user.principal();
        /* Null metadata */
        if (!principle.containsKey(KName.METADATA)) {
            return Ux.future(data);
        }

        final JsonObject metadata = principle.getJsonObject(KName.METADATA);

        /*
         * Replace uri and method to refresh parameters.
         * Here you must be replaced with:
         *    requestUri --> uri
         * Here the result is different
         */
        data.put(KName.URI, metadata.getString(KName.URI_REQUEST));
        data.put(KName.METHOD, metadata.getString(KName.METHOD));
        return Ux.future(data);
    }
}
