package io.zerows.extension.runtime.crud.uca.op.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KField;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.desk.IxReply;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.uca.op.Agonic;
import io.zerows.extension.runtime.crud.util.Ix;
import io.zerows.platform.constant.VName;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AgonicUpdate implements Agonic {

    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        return this.uniqueJAsync(input, in).compose(json -> {
            if (Ut.isNil(json)) {
                // 如果没有读取到可更新的记录，则直接返回 204 正常记录提取以保证API幂等性
                return IxReply.success204Pre();
            }


            /*
             * 深度合并两个Json对象
             * - input：输入的新数据
             * - json：从数据库中提取的已经存储的记录
             */
            final JsonObject combineJ = json.copy().mergeIn(input, true);
            return Ix.pass(combineJ, in,
                    Pre.audit(false)::inJAsync,                 // updatedAt, updatedBy
                    Pre.fileIn(false)::inJAsync               // File: Attachment creating
                )


                // 「AOP」带 AOP 的核心更新执行逻辑
                .compose(AgonicJq.updateFnJ(in));
        });
    }

    /**
     * 更新记录时，会直接调用底层提取数据的相关方法实现更新的核心逻辑
     * <pre><code>
     *     1. 根据 PrimaryKey 和 UniqueKey 从数据库中抓取数据信息，抓取过程中的优先级如：
     *        - PrimaryKey：先根据主键读取数据信息
     *        - UniqueKey：然后根据唯一键读取数据信息
     *     2. 本方法主要负责优先级的排列，以防止后续如果有其他模式读取信息的情况，此处的 UniqueKey
     *        和唯一键相关的 “标识规则” 定义有关 {@link KField} 中的多维标识符定义
     * </code></pre>
     * 更新数据过程中的数据提取和按ID提取其内部逻辑有所区别，所以此处的提取必须使用 “标识规则” 来完成
     * 整体数据提取流程。
     *
     * @param inputJ 输入的数据信息，查询条件
     * @param in     {@link IxMod} 模型信息
     *
     * @return {@link Future} 异步记录结果
     */
    private Future<JsonObject> uniqueJAsync(final JsonObject inputJ, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        /*
         * 此处特殊方法调用，peekJ 会执行优先方法调用，第一个方法返回 null 则继续执行，
         * 若是 JsonObject，则执行 Ut.isNil 的持续判断，否则直接返回第一个方法的结果。
         */
        return Ix.peekJ(inputJ, in,


            // 按主键读取数据记录
            (data, mod) -> Pre.qr(QrType.BY_PK)
                .inJAsync(data, mod).compose(jooq::fetchJOneAsync),


            // 按标识规则读取数据记录
            (data, mod) -> Pre.qr(QrType.BY_UK)
                .inJAsync(data, mod).compose(jooq::fetchJOneAsync)
        );
    }

    @Override
    public Future<JsonArray> runJAAsync(final JsonObject input, final IxMod in) {
        return this.uniqueAAsync(input, in)
            .compose(original -> {
                final KField fieldConfig = in.module().getField();
                final JsonArray matrix = Ix.onMatrix(fieldConfig);
                return Ux.compareJAsync(original, input.getJsonArray(KName.DATA), matrix);
            })
            .compose(compared -> {
                final JsonArray updateQueue = compared.get(ChangeFlag.UPDATE);
                return this.runAAsync(updateQueue, in);
            });
    }

    /**
     * 批量读取数据记录集，读取满足条件的所有记录集，此处不可以直接使用 {@link Pre}，原因在于该接口支持的三个提取方法主要针对单记录
     * <pre><code>
     *     1. 根据ID读取单记录
     *     2. 根据主键读取单记录
     *     3. 根据唯一键读取单记录
     * </code></pre>
     * 方法内置调用的是 {@link ADB#fetchJAsync}，返回值内置类型为 {@link JsonArray}
     *
     * @param inputJ 输入的数据信息（包含查询条件）
     * @param in     {@link IxMod} 模型信息
     *
     * @return {@link Future} 异步记录结果
     */
    private Future<JsonArray> uniqueAAsync(final JsonObject inputJ, final IxMod in) {
        final JsonObject query = inputJ.getJsonObject(VName.KEY_CRITERIA);
        LOG.Filter.info(this.getClass(), "( Mass Update ) Condition: {0}", query);
        final ADB jooq = IxPin.jooq(in);
        return jooq.fetchJAsync(query);
    }

    @Override
    public Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        return Ix.pass(input, in,
                Pre.audit(false)::inAAsync                      // updatedAt, updatedBy
                // 批量模式不考虑附件部分的操作
            )


            // 「AOP」带 AOP 的核心更新执行逻辑
            .compose(AgonicJq.updateFnA(in));
    }
}
