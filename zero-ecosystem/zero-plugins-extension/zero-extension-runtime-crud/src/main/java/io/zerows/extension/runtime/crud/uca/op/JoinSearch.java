package io.zerows.extension.runtime.crud.uca.op;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.support.fn.Fx;
import io.zerows.extension.runtime.crud.uca.dao.Operate;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.util.Ix;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class JoinSearch implements Agonic {
    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        LOG.Filter.info(this.getClass(), "( Search ) Condition: {0}", input);

        final Operate<JsonObject, JsonObject> operate = Operate.ofSearch();
        /* 内置做过 in.canJoin() 的判断，此处不再考虑连接的判断 */
        return operate.annexFn(in).apply(input)
            /*
             * 针对特殊节点的处理，metadata 节点
             * {
             *     "metadata": {}
             * }
             */
            .compose(Fx.ofPage(KName.METADATA))
            /*
             * 针对后期 Page 分页接口之后的序列化执行
             * {
             *     "list":  [],
             *     "count": ??
             * }
             */
            .compose(response -> Ux.future(Ix.serializeP(response, in.module(), in.connected())));
    }
}
