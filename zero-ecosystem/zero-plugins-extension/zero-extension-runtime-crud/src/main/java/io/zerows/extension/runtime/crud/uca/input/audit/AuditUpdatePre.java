package io.zerows.extension.runtime.crud.uca.input.audit;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.corpus.mbse.metadata.KModule;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.metadata.KField;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AuditUpdatePre extends AuditAction {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        /* UserId */
        this.auditor(data, in);
        return Ux.future(data);
    }

    private void auditor(final JsonObject data, final IxMod in) {
        /* UserId */
        final Envelop envelop = in.envelop();
        final User user = envelop.user();
        final KModule module = in.module();
        if (Objects.nonNull(user)) {
            final String userId = Ux.keyUser(user);
            if (Ut.isNotNil(userId)) {
                final KField field = module.getField();
                /* Created */
                this.setAuditor(data, field.getUpdated(), userId);
            }
        }
    }

    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        Ut.itJArray(data).forEach(json -> this.auditor(json, in));
        return Ux.future(data);
    }
}
