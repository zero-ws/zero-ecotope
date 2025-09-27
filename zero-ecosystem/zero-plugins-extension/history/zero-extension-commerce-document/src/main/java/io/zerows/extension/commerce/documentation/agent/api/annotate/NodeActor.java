package io.zerows.extension.commerce.documentation.agent.api.annotate;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.extension.commerce.documentation.domain.tables.daos.DCommentDao;
import io.zerows.extension.commerce.documentation.eon.Addr;

/**
 * @author lang : 2023-09-25
 */
@Queue
public class NodeActor {

    @Address(Addr.Comment.BY_MODEL)
    public Future<JsonArray> fetchByModel(final String modelId,
                                          final String modelKey) {
        final JsonObject qr = Ux.whereAnd();
        qr.put(KName.MODEL_ID, modelId);
        qr.put(KName.MODEL_KEY, modelKey);
        return Ux.Jooq.on(DCommentDao.class).fetchJAsync(qr);
    }
}
