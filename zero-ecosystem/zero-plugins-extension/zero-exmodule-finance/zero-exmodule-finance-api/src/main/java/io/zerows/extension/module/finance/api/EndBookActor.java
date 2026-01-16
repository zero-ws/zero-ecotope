package io.zerows.extension.module.finance.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.finance.domain.tables.daos.FBookDao;
import io.zerows.extension.module.finance.domain.tables.pojos.FBook;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.Instant;
import java.util.List;

/**
 * @author lang : 2023-09-04
 */
@Queue
public class EndBookActor {

    @Address(Addr.Settle.UP_BOOK)
    public Future<JsonArray> finalizeBook(final JsonArray books, final User user) {
        // Book Finalize ( Not Settlement )
        final String userKey = Account.userId(user);
        Ut.itJArray(books).forEach(json -> {
            json.put(KName.UPDATED_AT, Instant.now());
            json.put(KName.UPDATED_BY, userKey);
        });
        final List<FBook> bookList = Ux.fromJson(books, FBook.class);
        return DB.on(FBookDao.class).updateAsync(bookList).compose(Ux::futureA);
    }
}
