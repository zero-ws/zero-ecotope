package io.zerows.extension.runtime.ambient.uca.digital;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.fn.FnZero;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XTabularDao;
import io.zerows.unity.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AideApp extends AbstractAide {
    /*
     * Field = APP_ID
     */
    @Override
    public Future<JsonArray> fetch(final String field, final JsonArray types) {
        return this.fetchDict(this.condApp(field, types, null));
    }

    @Override
    public Future<JsonObject> fetch(final String field, final String type, final String code) {
        return Ux.Jooq.on(XTabularDao.class)
            .fetchOneAsync(this.condApp(field, type, code))
            .compose(Ux::futureJ).compose(FnZero.ofJObject(KName.METADATA));
    }
}
