package io.zerows.extension.runtime.ambient.uca.digital;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTabularDao;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;
import io.zerows.support.fn.Fx;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AideSigma extends AbstractAide {
    /*
     * Field = APP_ID
     */
    @Override
    public Future<JsonArray> fetch(final String field, final JsonArray types) {
        return this.fetchDict(this.condSigma(field, types, null));
    }

    @Override
    public Future<JsonObject> fetch(final String field, final String type, final String code) {
        return DB.on(XTabularDao.class)
            .fetchOneAsync(this.condSigma(field, type, code))
            .compose(Ux::futureJ).compose(Fx.ofJObject(KName.METADATA));
    }
}
