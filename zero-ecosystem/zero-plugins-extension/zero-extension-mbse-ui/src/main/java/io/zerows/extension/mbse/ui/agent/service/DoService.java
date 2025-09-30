package io.zerows.extension.mbse.ui.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.fn.FnZero;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.ui.bootstrap.UiPin;
import io.zerows.extension.mbse.ui.domain.tables.daos.UiOpDao;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiOp;
import io.zerows.unity.Ux;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DoService implements DoStub {
    private static final Annal LOGGER = Annal.get(DoService.class);

    // type = null, 旧的兼容流程
    @Override
    public Future<JsonArray> fetchSmart(final JsonObject params) {
        final String control = Ut.valueString(params, KName.Ui.CONTROL);
        if (Ut.isNil(control)) {
            return this.fetchWeb(params);
        } else {
            return this.fetchAtom(params);
        }
    }

    @Override
    public Future<JsonArray> fetchAtom(final JsonObject params) {
        final String control = Ut.valueString(params, KName.Ui.CONTROL);
        return Ux.Jooq.on(UiOpDao.class)
            .<UiOp>fetchAsync(KName.Ui.CONTROL_ID, control)
            .compose(Ux::futureA)
            .compose(FnZero.ofJArray(KName.Ui.CONFIG));
    }

    @Override
    public Future<JsonArray> fetchWeb(final JsonObject params) {
        final String identifier = Ut.valueString(params, KName.IDENTIFIER);
        LOG.Ui.info(LOGGER, "The fixed identifier = `{0}`", identifier);
        return Ux.future(UiPin.getOp());
    }

    @Override
    public Future<JsonArray> fetchFlow(final JsonObject params) {
        final String workflow = Ut.valueString(params, KName.Flow.WORKFLOW);
        final String task = Ut.valueString(params, KName.Flow.NODE);
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.Ui.CONTROL_ID, workflow);
        condition.put(KName.EVENT, task);
        LOG.Ui.info(LOGGER, "The workflow condition = `{0}`", condition.encode());
        return Ux.Jooq.on(UiOpDao.class)
            .<UiOp>fetchAsync(condition)
            .compose(Ux::futureA)
            .compose(FnZero.ofJArray(KName.Ui.CONFIG));
    }
}
