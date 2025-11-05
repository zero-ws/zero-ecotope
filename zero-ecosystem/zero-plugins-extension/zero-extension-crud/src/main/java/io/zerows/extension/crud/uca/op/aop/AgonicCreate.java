package io.zerows.extension.crud.uca.op.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.extension.crud.bootstrap.IxPin;
import io.zerows.extension.crud.eon.em.QrType;
import io.zerows.extension.crud.uca.desk.IxMod;
import io.zerows.extension.crud.uca.desk.IxReply;
import io.zerows.extension.crud.uca.input.Pre;
import io.zerows.extension.crud.uca.op.Agonic;
import io.zerows.extension.crud.uca.trans.Tran;
import io.zerows.extension.crud.util.Ix;
import io.zerows.mbse.metadata.KModule;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AgonicCreate implements Agonic {
    @Override
    public Future<JsonObject> runJAsync(final JsonObject input, final IxMod in) {
        final KModule module = in.module();
        return this.uniqueJAsync(input, in).compose(json -> {
            if (Ut.isNotNil(json)) {
                // 数据本身存在，返回 201 Created 响应，此处 201 针对业务标识
                return IxReply.success201Pre(json, module);
            }


            return Ix.pass(input, in,
                    // UUID 主键生成
                    Pre.key(true)::inJAsync,
                    // 反向引用模型树型
                    Pre.ref()::inJAsync,
                    // 编号生成
                    Pre.serial()::inJAsync,
                    // 创建：createdAt, createdBy
                    Pre.audit(true)::inJAsync,
                    // 更新：updatedAt, updatedBy
                    Pre.audit(false)::inJAsync,
                    // 附件: Attachment creating
                    Pre.fileIn(true)::inJAsync
                )


                // 「AOP」带 AOP 的核心添加执行函数
                .compose(AgonicJq.createFnJ(in));
        });
    }

    /**
     * 提取数据记录专用函数，由于是添加逻辑，所以不可能依赖主键的模式来提取数据，此处只能从业务上使用
     * 唯一键来处理数据记录并进行提取。
     *
     * @param input 输入的数据信息，查询条件
     * @param in    {@link IxMod} 模型信息
     *
     * @return {@link Future} 异步记录结果集
     */
    private Future<JsonObject> uniqueJAsync(final JsonObject input, final IxMod in) {
        final ADB jooq = IxPin.jooq(in);
        return Pre.qr(QrType.BY_UK).inJAsync(input, in)
            .compose(jooq::fetchJOneAsync);
    }

    @Override
    public Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        return Ix.pass(input, in,
                // UUID 主键生成
                Pre.key(true)::inAAsync,
                // 反向引用模型树型
                Pre.ref()::inAAsync,
                // UUID 生成后树型菜单
                Tran.tree(true)::inAAsync,
                // 编号生成
                Pre.serial()::inAAsync,
                // 创建：createdAt, createdBy
                Pre.audit(true)::inAAsync,
                // 附件: Attachment creating
                Pre.audit(false)::inAAsync
            )


            // 「AOP」带 AOP 的核心添加执行函数
            .compose(AgonicJq.createFnA(in));
    }
}
