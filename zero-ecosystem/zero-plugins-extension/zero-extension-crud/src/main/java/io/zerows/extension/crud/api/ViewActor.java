package io.zerows.extension.crud.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.crud.common.em.ApiSpec;
import io.zerows.extension.crud.uca.Agonic;
import io.zerows.extension.crud.uca.IxPanel;
import io.zerows.extension.crud.uca.IxRequest;
import io.zerows.extension.crud.uca.input.Pre;
import io.zerows.extension.crud.uca.next.Co;

/*
 * 「新版定制完成」
 */
@Queue
public class ViewActor {

    /*
     * GET: /api/columns/{actor}/full
     */
    @Address(Addr.Get.COLUMN_FULL)
    public Future<JsonArray> getFull(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_NONE).build(envelop);
        return ViewHelper.fetchFull(request).runJ(request.dataV());
    }

    /*
     * GET: /api/columns/{actor}/my
     */
    @Address(Addr.Get.COLUMN_MY)
    @SuppressWarnings("all")
    public Future<JsonArray> getMy(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_NONE).build(envelop);
        return IxPanel.on(request)
            .input(
                Pre.apeak(true)::inJAsync,              /* Apeak */
                Pre.head()::inJAsync                    /* Header */
            )
            /*
             * {
             *     "view": "The view name, if not put DEFAULT",
             *     "uri": "http path",
             *     "method": "http method",
             *     "sigma": "The application uniform"
             * }
             */
            .parallel(/* Active */Agonic.view(true)::runJAAsync, null)
            .output(/* Columns connected */Co.endV(true)::ok)
            .runJ(request.dataV());
    }
}

