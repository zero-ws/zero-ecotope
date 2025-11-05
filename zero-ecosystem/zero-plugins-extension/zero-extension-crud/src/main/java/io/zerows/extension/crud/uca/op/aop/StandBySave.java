package io.zerows.extension.crud.uca.op.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.destine.Conflate;
import io.zerows.component.destine.Hymn;
import io.zerows.epoch.metadata.KJoin;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.crud.bootstrap.IxPin;
import io.zerows.extension.crud.uca.desk.IxMod;
import io.zerows.extension.crud.uca.input.Pre;
import io.zerows.extension.crud.uca.op.Agonic;
import io.zerows.extension.crud.util.Ix;
import io.zerows.mbse.metadata.KModule;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

import static io.zerows.extension.crud.util.Ix.LOG;

/**
 * 此方法主要位于
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class StandBySave implements Agonic {
    private final transient IxMod module;

    StandBySave(final IxMod module) {
        this.module = module;
    }

    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        return this.uniqueJAsync(input, in).compose(json -> {
            final KModule standBy = in.module();
            // Avoid overwrite primary key here
            final JsonObject inputJ = input.copy();
            inputJ.remove(standBy.getField().getKey());
            if (Ut.isNil(json)) {
                // Not Found ( Insert )
                return Ix.pass(inputJ, in,
                        Pre.key(true)::inJAsync,                  // UUID Generated
                        Pre.serial()::inJAsync,                         // Serial/Number
                        Pre.audit(true)::inJAsync,              // createdAt, createdBy
                        Pre.audit(false)::inJAsync              // updatedAt, updatedBy
                    )


                    // 「AOP」带 AOP 的核心添加函数
                    .compose(AgonicJq.createFnJ(in));
            } else {
                // Found ( Update )
                final JsonObject combineJ = json.copy().mergeIn(inputJ, true);
                return Ix.pass(combineJ, in,
                        Pre.audit(false)::inJAsync         // updatedAt, updatedBy
                    )

                    // 「AOP」带 AOP 的核心更新函数
                    .compose(AgonicJq.updateFnJ(in));
            }
        });
    }

    /**
     * 提取 StandBy 记录，和原始记录提取不同点在于
     * <pre><code>
     *     1. StandBy 宽容度大很多，所以不存在 201 / 204 的响应信息
     *     2. 提取的条件依据输入数据进行实时运算，访问 {@link Conflate} 接口
     * </code></pre>
     *
     * @param input 输入数据
     * @param in    {@link IxMod} 模型
     *
     * @return {@link Future}
     */
    private Future<JsonObject> uniqueJAsync(final JsonObject input, final IxMod in) {
        // 查询条件处理
        final Conflate<JsonObject, JsonObject> conflate =
            Conflate.ofQr(this.module.connect(), false);
        final JsonObject condition = conflate.treat(input, this.module.connectId());


        // 此处读取 StandBy 相关记录
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchJOneAsync(condition);
    }

    /*
     * Fix:
     */
    @Override
    public Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        return this.uniqueAAsync(input, in).compose(queried -> {


            // 计算连接点部分内容
            final KJoin join = this.module.connect();
            if (Objects.isNull(join)) {
                // 未定义连接点，截断返回
                return Ux.future(input);
            }
            final Hymn<String> hymn = Hymn.ofString(join);
            final KJoin.Point point = hymn.pointer(this.module.connectId());
            if (Objects.isNull(point)) {
                // 连接点无法解析和计算，截断返回
                return Ux.future(input);
            }


            final String joinedKey = point.getKeyJoin();
            final JsonArray combined = Ux.updateJ(queried, input, joinedKey);

            // Update Combine InJson Data
            return Ix.pass(combined, in,
                    Pre.audit(false)::inAAsync                  // updatedAt, updatedBy
                )


                // 「AOP」带 AOP 的核心更新函数
                .compose(AgonicJq.updateFnA(in));
        });
    }

    private Future<JsonArray> uniqueAAsync(final JsonArray inputA, final IxMod in) {
        final Conflate<JsonArray, JsonObject> conflate =
            Conflate.ofQr(this.module.connect(), true);
        final JsonObject condition = conflate.treat(inputA, this.module.connectId());

        LOG.Filter.info(this.getClass(), "( Batch ) By Joined: identifier: {0}, condition: {1}", in.module().identifier(), condition);
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchJAsync(condition);
    }
}
