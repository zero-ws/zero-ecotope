package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.next.Co;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

@Queue
@SuppressWarnings("all")
public class PutActor {

    @Address(Addr.Put.BY_ID)
    public Future<Envelop> update(final Envelop envelop) {
        /* Module and Key Extract  */
        final IxRequest request = IxRequest.create(ApiSpec.BODY_WITH_KEY).build(envelop);
        final Co co = Co.nextJ(request.active(), false);
        return IxPanel.on(request)

            /*
             * 1. Input = JsonObject
             * -- io.vertx.mod.crud.operation.input.HeadPre
             * -- io.vertx.mod.crud.operation.input.CodexPre ( Validation )
             */
            .input(
                Pre.head()::inJAsync,                       /* Header */
                Pre.codex()::inJAsync                       /* Codex */
            )


            /*
             * 2. io.vertx.mod.crud.operation.next.NtJData
             * JsonObject ( active ) -> JsonObject ( standBy )
             */
            .next(in -> co::next)

            /*
             * 3. passion will set sequence = true
             *
             * (J) -> (J) Active (J) -> StandBy (J)
             *
             */
            .passion(
                /* Active */Agonic.write(ChangeFlag.UPDATE)::runJAsync,
                /* StandBy */Agonic.write(request.active())::runJAsync
            )


            /*
             * 4.1 The response is as following ( JsonObject Merged )
             */
            .output(co::ok)


            /*
             *
             * 0. Input
             *
             * JsonObject -> JsonObject -> JsonObject
             *
             */
            .<JsonObject, JsonObject, JsonObject>runJ(request.dataKJ())


            /*
             * 404 / 200
             */
            .compose(IxReply::successPost);
    }

    @Address(Addr.Put.BATCH)
    public Future<Envelop> updateBatch(final Envelop envelop) {
        /*
         * IxPanel processing building to split mass update
         * */
        final IxRequest request = IxRequest.create(ApiSpec.BODY_ARRAY).build(envelop);
        /*
         * 1. Fetch all original JsonArray data
         */
        return Agonic.fetch().runAAsync(request.dataA(), request.active()).compose(data -> {
            final IxPanel panel = IxPanel.on(request);
            final Co co = Co.nextJ(request.active(), true);
            /*
             * 2. Data Merge
             */
            final JsonArray parameters = Ux.updateJ(data, request.dataA());
            return panel


                /*
                 * 1. Input = JsonArray
                 * -- io.vertx.mod.crud.operation.input.HeadPre
                 * -- io.vertx.mod.crud.operation.input.CodexPre ( Validation )
                 */
                .input(
                    Pre.head()::inAAsync                       /* Header */
                )


                .next(in -> co::next)

                /* Active / StandBy Shared */
                .passion(
                    Agonic.write(ChangeFlag.UPDATE)::runAAsync,
                    Agonic.write(request.active())::runAAsync
                )

                .output(co::ok)
                /*
                 *
                 * 0. Input
                 *
                 * JsonArray -> JsonArray -> JsonArray
                 *
                 */
                .runA(parameters);
        });
    }

    @Address(Addr.Put.COLUMN_MY)
    public Future<JsonObject> updateColumn(final Envelop envelop) {
        final IxRequest request = IxRequest.create(ApiSpec.BODY_JSON).build(envelop);
        /*
         * Fix issue of Data Region
         * Because `projection` and `criteria` are both spec params
         *
         * The parameters data structure
         * {
         *      "data":  {
         *          "comment": "came from `viewData` field include view"
         *      },
         *      "impactUri": "The url that will be impact of my view, common is /api/xxx/search"
         * }
         * */
        final JsonObject params = request.dataV();
        final JsonObject requestData = request.dataJ();
        final JsonObject viewData = requestData.getJsonObject("viewData", new JsonObject());
        params.put(KName.DATA, Ut.valueJObject(viewData));
        params.put(KName.URI_IMPACT, viewData.getString(KName.URI_IMPACT));
        return IxPanel.on(request)

            /*
             * 1. Input = JsonObject
             * -- io.vertx.mod.crud.operation.input.ApeakMyPre
             * -- io.vertx.mod.crud.operation.input.CodexPre ( Validation )
             */
            .input(
                Pre.apeak(true)::inJAsync,              /* Apeak */
                Pre.head()::inJAsync                    /* Header */
            )

            /*
             * 2. 「Active Only」, no standBy defined
             */
            .passion(Agonic.view()::runJAsync, null)


            /*
             * 0. Input
             * JsonObject -> JsonObject -> JsonObject
             */
            .runJ(params);
    }
}
