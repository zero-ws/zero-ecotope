package io.zerows.epoch.component.expression;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import org.apache.commons.jexl3.JexlContext;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class InletFlow extends InletBase {
    InletFlow(final boolean isPrefix) {
        super(isPrefix);
    }

    @Override
    public void compile(final JexlContext context, final JsonObject data, final JsonObject config) {
        final JsonObject workflow = Ut.valueJObject(data, KName.Flow.WORKFLOW);
        final String zw = this.variable("zw");
        context.set(zw, Ut.toMap(workflow));
        this.console("[ Script ] ( Workflow ) The variable `{0}` has been bind: {1}", zw, workflow.encode());
    }
}
