package io.zerows.extension.runtime.crud.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.module.domain.atom.specification.KTransform;
import io.zerows.unity.Ux;

/**
 * Support Variable
 *
 * 1. module
 * 2. sigma
 * 3. language
 * 4. appId
 *
 * Other variable should use fixed value instead of expression
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class InitialTran implements Tran {

    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        // Modify data directly
        if (in.canTransform()) {
            Ut.itJArray(data).forEach(each -> this.initial(each, in));
        }
        return Ux.future(data);
    }

    private void initial(final JsonObject data, final IxMod in) {
        final KModule module = in.module();
        final KTransform transform = module.getTransform();
        final JsonObject exprTpl = transform.getInitial();
        if (Ut.isNil(exprTpl)) {
            return;
        }
        // Arguments Processing
        final JsonObject args = Ix.onParameters(in);
        final JsonObject parsed = Ut.fromExpression(exprTpl, args);
        final JsonObject appended = Ut.valueAppend(data, parsed);
        data.mergeIn(appended, true);
    }
}
