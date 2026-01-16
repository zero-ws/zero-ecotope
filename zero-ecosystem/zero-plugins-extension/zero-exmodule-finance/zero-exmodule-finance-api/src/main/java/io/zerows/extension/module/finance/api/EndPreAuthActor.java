package io.zerows.extension.module.finance.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.finance.common.FmConstant;
import io.zerows.extension.module.finance.domain.tables.daos.FPreAuthorizeDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.Instant;
import java.util.List;

/**
 * @author lang : 2024-01-11
 */
@Queue
public class EndPreAuthActor {

    @Address(Addr.Settle.UNLOCK_AUTHORIZE)
    public Future<JsonArray> unlockAuthorize(final JsonArray authorized, final User user) {
        // Authorized Modification
        final String userKey = Account.userId(user);
        Ut.itJArray(authorized).forEach(json -> {
            json.put(KName.UPDATED_AT, Instant.now());
            json.put(KName.UPDATED_BY, userKey);
            json.put(KName.STATUS, FmConstant.Status.FINISHED);
        });
        final List<FPreAuthorize> authorizeList = Ux.fromJson(authorized, FPreAuthorize.class);
        return DB.on(FPreAuthorizeDao.class).updateAsync(authorizeList).compose(Ux::futureA);
    }
}
