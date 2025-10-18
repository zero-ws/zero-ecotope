package io.zerows.extension.runtime.tpl.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.skeleton.eon.em.OwnerType;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExSetting;
import io.zerows.extension.runtime.tpl.domain.tables.daos.MyNotifyDao;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;

/**
 * @author lang : 2024-04-09
 */
public class ExSettingOfUser implements ExSetting {
    @Override
    public Future<JsonObject> settingAsync(final String user, final String dimKey) {
        final JsonObject response = new JsonObject();
        return this.settingNotify(user, dimKey).compose(notifySetting -> {
            response.put("notification", notifySetting);
            return Ux.future(response);
        });
    }

    private Future<JsonObject> settingNotify(final String user, final String dimKey) {
        final JsonObject params = Ux.whereAnd();
        params.put(KName.OWNER_ID, user);
        params.put(KName.SIGMA, dimKey);
        params.put(KName.OWNER_TYPE, OwnerType.USER);
        return DB.on(MyNotifyDao.class).fetchJOneAsync(params);
    }
}
