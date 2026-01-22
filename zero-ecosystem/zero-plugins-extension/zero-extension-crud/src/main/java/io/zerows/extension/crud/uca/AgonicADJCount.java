package io.zerows.extension.crud.uca;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

/**
 * 针对 COUNT 聚集的核心组件操作
 * <pre><code>
 *     1. 上层操作：{@link AgonicADJCount} 上层调用多出的内容：
 *        - 追加 AND 操作符
 *        - 打印查询条件
 *        - （调用下层）
 *        - 格式化返回数据结构
 *     2. 下层原子操作：{@see io.vertx.mod.crud.operation.dao.OperateCount}
 *        - 直接根据查询条件返回 COUNT 聚集结果
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class AgonicADJCount implements Agonic {
    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        // 追加 AND 操作符号，此步骤是 COUNT 方法独有的
        if (!input.containsKey(VString.EMPTY)) {
            input.put(VString.EMPTY, Boolean.TRUE);
        }

        log.info("{} / 统计 Count -> 条件：{}", IxConstant.K_PREFIX, input);


        final Operate<JsonObject, Long> operate = Operate.ofCount();
        /* 内置已经做过 in.canJoin() 的判断，所以此处不再考虑连接的判断 **/
        return operate.annexFn(in).apply(input)
            /*
             * 返回数据结构：
             * {
             *     "count": ???
             * }
             */
            .compose(counter -> Ux.future(new JsonObject().put(KName.COUNT, counter)));
    }
}
