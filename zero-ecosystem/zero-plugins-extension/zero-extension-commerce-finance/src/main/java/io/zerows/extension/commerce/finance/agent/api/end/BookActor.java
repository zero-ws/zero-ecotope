package io.zerows.extension.commerce.finance.agent.api.end;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.finance.domain.tables.daos.FBookDao;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FBook;
import io.zerows.extension.commerce.finance.eon.Addr;
import io.zerows.unity.Ux;

import java.time.Instant;
import java.util.List;

/**
 * @author lang : 2023-09-04
 */
@Queue
public class BookActor {

    @Address(Addr.Settle.UP_BOOK)
    public Future<JsonArray> finalizeBook(final JsonArray books, final User user) {
        // Book Finalize ( Not Settlement )
        final String userKey = Ux.keyUser(user);
        Ut.itJArray(books).forEach(json -> {
            json.put(KName.UPDATED_AT, Instant.now());
            json.put(KName.UPDATED_BY, userKey);
        });
        final List<FBook> bookList = Ux.fromJson(books, FBook.class);
        return Ux.Jooq.on(FBookDao.class).updateAsync(bookList).compose(Ux::futureA);
    }
}
