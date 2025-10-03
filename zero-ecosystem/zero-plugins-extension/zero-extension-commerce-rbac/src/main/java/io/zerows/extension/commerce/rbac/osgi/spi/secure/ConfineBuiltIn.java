package io.zerows.extension.commerce.rbac.osgi.spi.secure;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.skeleton.osgi.spi.modeler.Confine;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ConfineBuiltIn implements Confine {
    /*
     * 1. 直接根据 request 中的数据和 syntax 中定义的模板执行解析
     * 2. 走 JEXL 流程，语法数据结构
     * {
     *     "phase": "AFTER / BEFORE",
     *     "data": {
     *         - type
     *         - viewId
     *         - seekKey
     *         - identifier
     *     }
     * }
     */
    @Override
    public Future<JsonObject> restrict(final JsonObject request, final JsonObject syntax) {
        // 标准化执行处理
        final JsonObject exprTpl = Ut.valueJObject(syntax, KName.DATA);
        final JsonObject condition = Ut.fromExpression(exprTpl, request);
        LOG.View.info(this.getClass(), "( BuiltIn ) Visitant unique query condition: {0}", condition);
        return Ux.future(condition);
    }
}
