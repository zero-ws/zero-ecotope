package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;

/*
 * JqTool Engine for
 * 1) Pagination
 * 2) Projection
 * 3) Sorter
 * 4) Criteria
 *
 * The parameter formatFail is as:
 * {
 *      "criteria":{},
 *      "projection":[],
 *      "sorter":[],
 *      "pager":{
 *          "size":10,
 *          "page":1
 *      }
 * }
 * 「新版定制完成」
 */
@Queue
public class QueryActor {
    /*
     * POST: /api/{actor}/search
     *      200: Search Record
     */
    @Address(Addr.Post.SEARCH)
    public Future<Envelop> search(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_JSON).build(envelop);
        return IxPanel.on(request)
            .input(
                Pre.codex()::inJAsync                   /* Codex */
            )
            .passion(Agonic.search()::runJAsync, null)
            .runJ(request.dataJ());
    }

    @Address(Addr.Post.EXISTING)
    public Future<Envelop> existing(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_JSON).build(envelop);
        return IxPanel.on(request)
            .input(
                Pre.codex()::inJAsync                   /* Codex */
            )
            .passion(Agonic.count()::runJAsync, null)
            .<JsonObject, JsonObject, JsonObject>runJ(request.dataJ())
            .compose(item -> Ux.future(Envelop.success(0 < item.getInteger(KName.COUNT))));
    }

    @Address(Addr.Post.MISSING)
    public Future<Envelop> missing(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_JSON).build(envelop);
        return IxPanel.on(request)
            .input(
                Pre.codex()::inJAsync                   /* Codex */
            )
            .passion(Agonic.count()::runJAsync, null)
            .<JsonObject, JsonObject, JsonObject>runJ(request.dataJ())
            .compose(item -> Ux.future(Envelop.success(0 == item.getInteger(KName.COUNT))));
    }
}
