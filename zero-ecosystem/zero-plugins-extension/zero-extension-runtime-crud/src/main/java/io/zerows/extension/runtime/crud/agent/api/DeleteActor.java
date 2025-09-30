package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.em.typed.ChangeFlag;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;

@Queue
public class DeleteActor {
    /*
     * DELETE: /api/{actor}/{key}
     *     200: Delete existing record
     *     204: Second deleting, The record has been gone
     */
    @Address(Addr.Delete.BY_ID)
    public Future<Envelop> delete(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_STRING).build(envelop);
        final JsonObject params = request.dataK();
        return IxPanel.on(request)
            .passion(Agonic.write(ChangeFlag.DELETE)::runJAsync, null)
            .<JsonObject, JsonObject, JsonObject>runJ(params)
            /*
             * 204 / 200
             */
            .compose(IxReply::successPostB);
    }

    /*
     * DELETE: /api/batch/{actor}/delete
     *     200: Delete existing records
     *     204: Second deleting, The records have been gone
     */
    @Address(Addr.Delete.BATCH)
    public Future<Envelop> deleteBatch(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_ARRAY).build(envelop);
        return IxPanel.on(request)
            .input(
                Pre.qr(QrType.BY_PK)::inAJAsync                        /* keys,in */
            )
            .passion(Agonic.write(ChangeFlag.DELETE)::runJAAsync, null)
            .runA(request.dataA());
    }
}
