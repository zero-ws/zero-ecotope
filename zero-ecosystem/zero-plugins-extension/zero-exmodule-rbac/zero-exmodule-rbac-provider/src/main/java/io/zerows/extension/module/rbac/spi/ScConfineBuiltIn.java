package io.zerows.extension.module.rbac.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.skeleton.spi.ScConfine;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class ScConfineBuiltIn implements ScConfine {
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
        log.info("{} / BuildIn 资源访问者唯一条件：{}", ScConstant.K_PREFIX, condition);
        return Ux.future(condition);
    }
}
