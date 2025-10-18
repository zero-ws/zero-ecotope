package io.zerows.extension.runtime.crud.uca.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.DBJoin;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.mbse.metadata.KModule;

import java.util.function.Function;

/**
 * 针对系统底层的 COUNT 聚集操作，此处生成的函数支持查询引擎语法，Zero中的查询引擎语法参考教程中的书写
 * <pre><code>
 *     1. condition：查询条件，对应查询引擎中的 query -> criteria 部分。
 *     2. 返回 Long 类型：统计当前环境中符合条件的记录数量。
 * </code></pre>
 *
 * @author lang : 2023-08-02
 */
class OperateCount implements Operate<JsonObject, Long> {
    @Override
    public Function<JsonObject, Future<Long>> annexFn(final IxMod in) {
        return condition -> {
            // KModule
            if (in.canJoin()) {


                // Join 模式，这种情况下 in.connected() 不可能为 null
                final KModule connect = in.connected();
                final DBJoin join = IxPin.join(in, connect);
                return join.countAsync(condition);
            } else {


                // Direct 模式
                final DBJooq jooq = IxPin.jooq(in);
                return jooq.countAsync(condition);
            }
        };
    }
}
