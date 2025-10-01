package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;

/*
 * 「新版定制完成」
 */
@Queue
public class GetActor {

    /*
     * GET: /api/{actor}/{key}
     *     200: JqTool Data
     *     204: JqTool No Data
     */
    @Address(Addr.Get.BY_ID)
    public Future<Envelop> getById(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_STRING).build(envelop);
        return IxPanel.on(request)
            .passion(Agonic.get()::runJAsync, null)
            .<JsonObject, JsonObject, JsonObject>runJ(request.dataK())
            /*
             * 204 / 200
             */
            .compose(IxReply::successPost);
    }

    /*
     * GET: /api/{actor}/by/sigma
     *      200: JqTool All
     */
    @Address(Addr.Get.BY_SIGMA)
    public Future<Envelop> getAll(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_NONE).build(envelop);
        /* Headers */
        final JsonObject headers = envelop.headersX();
        final String sigma = headers.getString(KName.SIGMA);
        if (Ut.isNil(sigma)) {
            return Ux.future(Envelop.success(new JsonArray()));
        }
        return IxPanel.on(request)
            .input(
                /* Build Condition for All */
                Pre.qr(QrType.ALL)::inJAsync
            )
            .passion(Agonic.fetch()::runJAAsync, null)
            .runJ(new JsonObject().put(KName.SIGMA, sigma));
    }

}
