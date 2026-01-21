package io.zerows.extension.crud.uca;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.crud.common.em.QrType;
import io.zerows.extension.crud.uca.input.Pre;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class AgonicADJFetch implements Agonic {
    @Override
    public Future<JsonArray> runJAAsync(final JsonObject input, final IxMod in) {
        log.info("{} [ 全量 ] 条件：{}", IxConstant.K_PREFIX, input);

        final Operate<JsonObject, JsonArray> operate = Operate.ofFetch();
        /* 内置已经做过 in.canJoin() 的判断，所以此处不再考虑连接判断 */
        return operate.annexFn(in).apply(input);
    }

    @Override
    public Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        /*
         * 按 Primary Key 做查询，查询一堆数据用于后续操作
         */
        return Pre.qr(QrType.BY_PK).inAJAsync(input, in)
            .compose(condition -> this.runJAAsync(condition, in));
    }
}
