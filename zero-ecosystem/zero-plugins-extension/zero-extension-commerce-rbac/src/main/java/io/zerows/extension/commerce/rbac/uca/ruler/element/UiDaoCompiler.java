package io.zerows.extension.commerce.rbac.uca.ruler.element;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.store.module.OCacheDao;
import io.zerows.extension.commerce.rbac.exception._80226Exception404AdmitDaoNull;
import io.zerows.unity.Ux;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class UiDaoCompiler implements HAdmitCompiler {
    @Override
    public Future<JsonArray> ingest(final JsonObject qr, final JsonObject config) {
        final String daoStr = Ut.valueString(config, KName.DAO);
        /*
         * 新版此处要变更成 dao / table 双开的模式，核心调用代码
         * - final Class<?> daoCls = OCacheDao.findDao(daoStr);
         * 此处的 daoStr 属性支持两种不同的模式
         * - dao 为表名（简易模式）
         * - dao 为类全名（完整模式）
         */
        final Class<?> daoCls = OCacheDao.findDao(daoStr); // Ut.clazz(daoStr, null);

        // Error-80226, uiConfig中没有配置dao节点
        Fn.jvmKo(Objects.isNull(daoCls), _80226Exception404AdmitDaoNull.class, daoStr);
        return Ux.Jooq.on(daoCls).fetchJAsync(qr);
    }
}
