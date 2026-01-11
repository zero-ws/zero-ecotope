package io.zerows.extension.module.ui.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ui.boot.MDUIManager;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.extension.module.ui.domain.tables.daos.UiOpDao;
import io.zerows.extension.module.ui.domain.tables.pojos.UiOp;
import io.zerows.extension.module.ui.servicespec.DoStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class DoService implements DoStub {
    private static final MDUIManager MANAGER = MDUIManager.of();

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
        return DB.on(UiOpDao.class)
            .<UiOp>fetchAsync(KName.Ui.CONTROL_ID, control)
            .compose(Ux::futureA)
            .map(item -> Ut.valueToJArray(item, KName.Ui.CONFIG));
    }

    @Override
    public Future<JsonArray> fetchWeb(final JsonObject params) {
        final String identifier = Ut.valueString(params, KName.IDENTIFIER);
        log.info("{} 固定标识符 identifier = `{}`", UiConstant.K_PREFIX_UI, identifier);
        return Ux.future(MANAGER.getOp());
    }

    @Override
    public Future<JsonArray> fetchFlow(final JsonObject params) {
        final String workflow = Ut.valueString(params, KName.Flow.WORKFLOW);
        final String task = Ut.valueString(params, KName.Flow.NODE);
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.Ui.CONTROL_ID, workflow);
        condition.put(KName.EVENT, task);
        log.info("{} 工作流条件 condition = `{}`, 节点 task = `{}`",
            UiConstant.K_PREFIX_UI, condition.encode(), workflow + "/" + task);
        return DB.on(UiOpDao.class)
            .<UiOp>fetchAsync(condition)
            .compose(Ux::futureA)
            .map(item -> Ut.valueToJArray(item, KName.Ui.CONFIG));
    }
}
