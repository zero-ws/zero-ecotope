package io.zerows.extension.crud.uca;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.crud.common.Ix;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class AgonicADJSearch implements Agonic {
    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        log.info("{} [ 搜索 ] 条件：{}", IxConstant.K_PREFIX, input);

        final Operate<JsonObject, JsonObject> operate = Operate.ofSearch();
        /* 内置做过 in.canJoin() 的判断，此处不再考虑连接的判断 */
        return operate.annexFn(in).apply(input)
            /*
             * 针对特殊节点的处理，metadata 节点
             * {
             *     "metadata": {}
             * }
             */
            .map(item -> Ut.valueToPage(item, KName.METADATA))
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
