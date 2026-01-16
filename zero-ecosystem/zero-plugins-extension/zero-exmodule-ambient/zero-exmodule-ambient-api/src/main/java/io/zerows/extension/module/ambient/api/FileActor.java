package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.ambient.domain.tables.daos.XAttachmentDao;
import io.zerows.extension.skeleton.common.enums.FileStatus;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class FileActor {


    @Address(Addr.File.MY_QUEUE)
    public Future<JsonObject> searchMy(final JsonObject query,
                                       final User user) {
        // JsonObject join `my queue` criteria
        final JsonObject qrDefault = Ux.whereAnd();
        qrDefault.put(KName.STATUS, FileStatus.DONE.name());
        qrDefault.put(KName.ACTIVE, Boolean.TRUE);
        qrDefault.put(KName.CREATED_BY, Account.userId(user));
        final JsonObject qrCombine = Ut.irAndQH(query, "$DFT$", qrDefault);
        return DB.on(XAttachmentDao.class).searchJAsync(qrCombine);
    }

    @Address(Addr.File.BY_KEY)
    public Future<JsonObject> fileByKey(final String key) {
        return DB.on(XAttachmentDao.class).fetchJByIdAsync(key);
    }
}
