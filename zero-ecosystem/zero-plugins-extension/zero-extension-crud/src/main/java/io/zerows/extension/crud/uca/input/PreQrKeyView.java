package io.zerows.extension.crud.uca.input;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.crud.common.Ix;
import io.zerows.extension.crud.common.IxMsg;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;

import static io.zerows.extension.crud.common.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class PreQrKeyView implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Kv<String, HttpMethod> impactUri = Ix.onImpact(in);
        final String sessionKey = Ke.keyView(
            impactUri.value().name(),
            impactUri.key(),
            KView.smart(data.getValue(KName.VIEW))
        );
        LOG.Dao.info(this.getClass(), IxMsg.CACHE_KEY_PROJECTION, sessionKey);
        data.put(KName.DATA_KEY, sessionKey);
        return Ux.future(data);
    }
}
