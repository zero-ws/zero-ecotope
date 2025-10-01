package io.zerows.extension.runtime.crud.uca.op;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.typed.ChangeFlag;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.op.aop.AgonicAop;
import io.zerows.extension.runtime.crud.uca.op.view.AgonicView;

/**
 * 此处有一个特殊点需要说明，关于这些组件命名有特殊约定
 * <pre><code>
 *     1. 带 Join 前缀的内置使用了 JOIN 语法，双表或多表直接执行 JOIN
 *     2. 带 Step 为步骤执行，优先考虑主表执行，再考虑子表执行
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Agonic {

    int EXPIRED = 2 * 60 * 60;

    static Agonic write(final ChangeFlag flag) {
        return AgonicAop.write(flag);
    }

    static Agonic write(final IxMod module) {
        return AgonicAop.write(module);
    }

    static Agonic file() {
        return Pooled.CCT_AGONIC.pick(StepImport::new, StepImport.class.getName());
    }

    static Agonic get() {
        return Pooled.CCT_AGONIC.pick(StepByID::new, StepByID.class.getName());
    }

    static Agonic search() {
        return Pooled.CCT_AGONIC.pick(JoinSearch::new, JoinSearch.class.getName());
    }

    static Agonic count() {
        return Pooled.CCT_AGONIC.pick(JoinCount::new, JoinCount.class.getName());
    }

    static Agonic view(final boolean isMy) {
        return AgonicView.view(isMy);
    }

    static Agonic view() {
        return AgonicView.view();
    }

    static Agonic fetch() {
        return Pooled.CCT_AGONIC.pick(JoinFetch::new, JoinFetch.class.getName());
    }

    default Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<JsonArray> runJAAsync(final JsonObject input, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<JsonObject> runAJAsync(final JsonArray input, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
