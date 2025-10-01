package io.zerows.extension.runtime.crud.uca.op.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.database.jooq.operation.UxJooq;
import io.zerows.epoch.common.uca.aop.Aspect;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.dao.Operate;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.unity.Ux;

import java.util.function.Function;

/**
 * @author lang : 2023-08-08
 */
class AgonicJq {
    // 添加
    static Function<JsonObject, Future<JsonObject>> createFnJ(final IxMod in) {
        final KModule module = in.module();
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(module, Aspect::wrapJCreate,
            // AOP中的核心函数
            aopJ -> Ix.deserializeT(aopJ, module)
                .compose(jooq::insertAsync)
                .compose(entity -> IxReply.successJ(entity, module))
        );
    }

    static Function<JsonArray, Future<JsonArray>> createFnA(final IxMod in) {
        final KModule module = in.module();
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(module, Aspect::wrapACreate,
            // AOP中的核心函数
            aopA -> Ix.deserializeT(aopA, module)
                .compose(jooq::insertAsync)
                .compose(inserted -> IxReply.successA(inserted, module))
        );
    }

    // 删除
    static Function<JsonObject, Future<JsonObject>> deleteFnJ(
        final JsonObject criteria, final IxMod in) {
        final KModule module = in.module();
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(module, Aspect::wrapJDelete,
            // AOP中的核心逻辑函数
            aopJ -> {
                final Operate<Object, Boolean> operate = Operate.ofDelete();
                return operate.annexFn(in).apply(aopJ)                  // 先删除关联记录
                    .compose(nil -> jooq.deleteByAsync(criteria))       // 再删除当前记录
                    .compose(IxReply::success200Pre);
            }
        );
    }

    static Function<JsonArray, Future<JsonArray>> deleteFnA(
        final JsonObject criteria, final IxMod in) {
        final KModule module = in.module();
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(module, Aspect::wrapADelete,
            // AOP中的核心逻辑函数
            aopJ -> {
                final Operate<Object, Boolean> operate = Operate.ofDelete();
                return operate.annexFn(in).apply(aopJ)                  // 先删除关联记录
                    .compose(nil -> jooq.deleteByAsync(criteria))       // 再删除当前记录
                    .compose(nil -> Ux.futureA());
            }
        );
    }

    // 更新
    static Function<JsonObject, Future<JsonObject>> updateFnJ(final IxMod in) {
        final KModule module = in.module();
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(module, Aspect::wrapJUpdate,
            // AOP中的核心逻辑函数
            aopJ -> Ix.deserializeT(aopJ, module)
                .compose(jooq::updateAsync)
                .compose(updated -> IxReply.successJ(updated, module))
        );
    }

    static Function<JsonArray, Future<JsonArray>> updateFnA(final IxMod in) {
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.aop(in.module(), Aspect::wrapAUpdate,
            // AOP中的核心逻辑函数
            aopA -> Ix.deserializeT(aopA, in.module())
                .compose(jooq::updateAsync)
                .compose(updated -> IxReply.successA(updated, in.module()))
        );
    }
}
