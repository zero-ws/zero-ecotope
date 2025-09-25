package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.next.Co;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;

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
        return T.fetchFull(request).runJ(request.dataV());
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

class T {
    /*
     * Shared Method mask as static method for two usage
     */
    @SuppressWarnings("all")
    static IxPanel fetchFull(final IxRequest request) {
        return IxPanel.on(request)
            .input(
                Pre.apeak(false)::inJAsync,             /* Apeak */
                Pre.head()::inJAsync                    /* Header */
            )
            /*
             * {
             *     "identifier": "Model identifier",
             *     "view": "The view name, if not put DEFAULT",
             *     "dynamic": "true if use dynamic",
             *     "sigma": "The application uniform"
             * }
             */
            .parallel(/* Active */Agonic.view(false)::runJAAsync)
            .output(/* Columns connected */Co.endV(false)::ok);
    }
}
