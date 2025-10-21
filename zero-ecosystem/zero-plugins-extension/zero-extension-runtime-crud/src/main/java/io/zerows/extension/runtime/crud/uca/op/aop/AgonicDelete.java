package io.zerows.extension.runtime.crud.uca.op.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.extension.skeleton.spi.ExTrash;
import io.zerows.mbse.metadata.KModule;
import io.zerows.program.Ux;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AgonicDelete implements Agonic {
    @Override
    public Future<JsonObject> runJAsync(final JsonObject criteria, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchOneAsync(criteria).compose(queried -> {
            if (Objects.isNull(queried)) {
                /*
                 * 截断返回，直接返回 204 的正常响应，实际就是 {}
                 * {
                 *     "status": 204,
                 *     "result": true
                 * }
                 */
                return IxReply.success204Pre(Boolean.TRUE);
            }

            final KModule module = in.module();
            final JsonObject json = Ix.serializeJ(queried, module);

            // 附件移除
            return Pre.fileOut().inJAsync(json, in)

                // 如果打开了 Trash 功能，则执行 Trash 的备份
                .compose(removed -> Ux.channelA(ExTrash.class, () -> Ux.future(removed),
                    (stub) -> stub.backupAsync(module.identifier(), removed)))

                // 「AOP」带 AOP 的核心删除执行逻辑
                .compose(AgonicJq.deleteFnJ(criteria, in));
        });
    }

    @Override
    public Future<JsonArray> runJAAsync(final JsonObject criteria, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchAsync(criteria).compose(queried -> {
            if (Objects.isNull(queried) || queried.isEmpty()) {
                return Ux.futureA();
            }

            final KModule module = in.module();
            final JsonArray array = Ix.serializeA(queried, module);

            // 附件移除
            return Pre.fileOut().inAAsync(array, in)


                // 如果打开了 Trash 功能，则执行 Trash 的备份
                .compose(removed -> Ux.channelA(ExTrash.class, () -> Ux.future(array),
                    stub -> stub.backupAsync(module.identifier(), array)))


                // 「AOP」带 AOP 的核心删除执行逻辑
                .compose(AgonicJq.deleteFnA(criteria, in))
                .compose(nil -> Ux.future(array));
        });
    }
}
