package io.zerows.extension.runtime.crud.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.runtime.crud.eon.Addr;
import io.zerows.extension.runtime.crud.eon.em.ApiSpec;
import io.zerows.extension.runtime.crud.uca.desk.IxPanel;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.desk.IxRequest;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.next.Co;
import io.zerows.extension.runtime.crud.uca.op.Agonic;

/*
 * Create new Record that defined in zero system.
 * The definition file are stored under
 *      `plugin/crud/module/`
 * The validation rule file are stored under
 *      `plugin/crud/validator/`
 * 「新版定制完成」
 */
@Queue
@SuppressWarnings("all")
public class PostActor {

    /*
     * POST: /api/{actor}
     *     200: Created new record
     *     201: The record existing in database ( Could not do any things )
     */
    @Address(Addr.Post.ADD)
    public Future<Envelop> create(final Envelop envelop) {
        /* Actor Extraction */
        final IxRequest request = IxRequest.create(ApiSpec.BODY_JSON).build(envelop);
        final Co coJ = Co.nextJ(request.active(), false);
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
            .next(in -> coJ::next)


            /*
             * 3. passion will set sequence = true
             *
             * (J) -> (J) Active (J) -> StandBy (J)
             *
             */
            .passion(Agonic.write(ChangeFlag.ADD)::runJAsync)


            /*
             * 4.1 The response is as following ( JsonObject Merged )
             */
            .output(coJ::ok)


            /*
             * 0. Input
             *
             * JsonObject -> JsonObject -> JsonObject
             */
            .<JsonObject, JsonObject, JsonObject>runJ(request.dataJ())


            /*
             * 4.2. 201 / 200
             */
            .compose(IxReply::successPost);
    }
}
