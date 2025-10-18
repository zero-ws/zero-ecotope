package io.zerows.extension.runtime.crud.uca.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.operation.DBJoin;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.mbse.metadata.KModule;

import java.util.function.Function;

/**
 * 针对底层的 SELECT 查询操作，此处的查询条件仅支持 Qr 中的 criteria 部分（非完整语法）
 *
 * @author lang : 2023-08-03
 */
class OperateFetch implements Operate<JsonObject, JsonArray> {
    @Override
    public Function<JsonObject, Future<JsonArray>> annexFn(final IxMod in) {
        return condition -> {
            // KModule
            if (in.canJoin()) {


                // Join 模式，此种情况下 in.connected() 不可能为 null
                final KModule connect = in.connected();
                final DBJoin join = IxPin.join(in, connect);
                return join.fetchAsync(condition);
            } else {


                // Direct 模式
                final DBJooq jooq = IxPin.jooq(in);
                return jooq.fetchJAsync(condition);
            }
        };
    }
}
