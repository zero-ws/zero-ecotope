package io.zerows.extension.crud.uca.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.destine.Conflate;
import io.zerows.component.destine.Hymn;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.crud.bootstrap.IxPin;
import io.zerows.extension.crud.uca.desk.IxMod;
import io.zerows.mbse.metadata.KModule;
import io.zerows.program.Ux;

import java.util.Objects;
import java.util.function.Function;

/**
 * Join模式下的进一步删除操作，此处删除方法替换原始的 seekFn 方法
 *
 * @author lang : 2023-08-04
 */
class SingleDelete implements Operate<Object, Boolean> {
    @Override
    public Function<Object, Future<Boolean>> annexFn(final IxMod in) {
        return input -> {
            if (!in.canJoin()) {
                // 非 JOIN 模式，直接返回 FALSE 的默认值
                return Ux.futureF();
            }

            // 构造删除专用的查询条件（动态模式）
            final ADB jooq = this.jooq(input, in);

            // 构造查询条件
            final JsonObject condition;
            if (input instanceof final JsonObject inputJ) {
                condition = this.conditionJ(inputJ, in);
            } else {
                final JsonArray inputA = (JsonArray) input;
                condition = this.conditionA(inputA, in);
            }
            // 执行删除
            return jooq.deleteByAsync(condition);
        };
    }

    private ADB jooq(final Object inputJ, final IxMod in) {
        final KModule module = in.module();
        final KJoin join = module.getConnect();
        final KJoin.Point point;
        if (inputJ instanceof final JsonObject objectJ) {
            final Hymn<JsonObject> hymn = Hymn.ofJObject(join);
            point = hymn.pointer(objectJ);
        } else {
            final JsonArray objectA = (JsonArray) inputJ;
            final Hymn<JsonArray> hymn = Hymn.ofJArray(join);
            point = hymn.pointer(objectA);
        }
        Objects.requireNonNull(point);

        final KModule switched = IxPin.getActor(point.getCrud());
        final ADB switchedJq = IxPin.jooq(switched, in.envelop());
        // 绑定 pojo
        // switchedJq.on(switched.getPojo());
        return switchedJq;
    }

    private JsonObject conditionJ(final JsonObject inputJ, final IxMod in) {
        // 提取模型
        final KModule module = in.module();
        final KJoin join = module.getConnect();

        final Conflate<JsonObject, JsonObject> conflate =
            Conflate.ofQr(join, false);

        // 构造 connect 的模型相关的核心模型
        return conflate.treat(inputJ, in.connectId());
    }

    private JsonObject conditionA(final JsonArray inputA, final IxMod in) {
        // 提取模型
        final KModule module = in.module();
        final KJoin join = module.getConnect();

        final Conflate<JsonArray, JsonObject> conflate =
            Conflate.ofQr(join, true);

        // 构造 connect 的模型相关的核心模型
        // 注：此处的条件 "": false 没有必要
        return conflate.treat(inputA, in.connectId());
    }
}
