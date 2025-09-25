package io.zerows.extension.commerce.documentation.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.documentation.domain.tables.daos.DDocClauseDao;
import io.zerows.extension.commerce.documentation.domain.tables.pojos.DDocClause;
import io.zerows.extension.commerce.documentation.eon.em.EmRefer;
import io.zerows.extension.commerce.documentation.uca.refer.Quote;

import java.util.List;
import java.util.Set;

/**
 * @author lang : 2023-09-25
 */
public class ClauseService implements ClauseStub {

    @Override
    public Future<JsonArray> createAsync(final JsonArray dataA, final JsonObject record) {
        final List<DDocClause> clauses = Ux.fromJson(dataA, DDocClause.class);
        return Ux.Jooq.on(DDocClauseDao.class).insertAsync(clauses).compose(Ux::futureA).compose(inserted -> {
            if (Ut.isNotNil(inserted)) {
                final Quote quote = Quote.of(EmRefer.Entity.DOC);
                return quote.plugAsync(record, inserted, EmRefer.Entity.CLAUSE)
                    .compose(nil -> Ux.future(inserted));
            } else {
                return Ux.future(inserted);
            }
        });
    }

    @Override
    public Future<JsonArray> updateAsync(final JsonArray dataA, final JsonObject record) {
        final List<DDocClause> clauses = Ux.fromJson(dataA, DDocClause.class);
        return Ux.Jooq.on(DDocClauseDao.class).updateAsync(clauses).compose(Ux::futureA);
    }

    @Override
    public Future<JsonArray> fetchByDoc(final String docKey) {
        final Quote quote = Quote.of(EmRefer.Entity.DOC);
        return quote.fetchAsync(docKey, EmRefer.Entity.CLAUSE).compose(refer -> {
            final Set<String> ids = Ut.valueSetString(refer, "toId");
            return Ux.Jooq.on(DDocClauseDao.class).fetchJInAsync(KName.KEY, ids);
        });
    }

    @Override
    public Future<Boolean> removeByKeys(final String docKey, final Set<String> keys) {
        final Quote quote = Quote.of(EmRefer.Entity.DOC);
        return quote.removeAsync(docKey, keys, EmRefer.Entity.CLAUSE).compose(nil -> {
            final JsonObject qr = Ux.whereAnd();
            qr.put(KName.KEY + ",i", Ut.toJArray(keys));
            return Ux.Jooq.on(DDocClauseDao.class).deleteByAsync(qr);
        });
    }
}
