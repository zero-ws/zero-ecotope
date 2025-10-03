package io.zerows.extension.runtime.crud.uca.input.qr;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.metadata.commune.Vis;
import io.zerows.extension.runtime.crud.eon.IxMsg;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.extension.runtime.skeleton.refine.Ke;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class KeyViewPre implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Kv<String, HttpMethod> impactUri = Ix.onImpact(in);
        final String sessionKey = Ke.keyView(
            impactUri.value().name(),
            impactUri.key(),
            Vis.smart(data.getValue(KName.VIEW))
        );
        LOG.Dao.info(this.getClass(), IxMsg.CACHE_KEY_PROJECTION, sessionKey);
        data.put(KName.DATA_KEY, sessionKey);
        return Ux.future(data);
    }
}
