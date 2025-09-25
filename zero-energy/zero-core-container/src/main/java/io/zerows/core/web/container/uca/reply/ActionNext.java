package io.zerows.core.web.container.uca.reply;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.commune.Envelop;

/**
 * @author lang : 2024-06-27
 */
public class ActionNext implements OAmbit {

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        /*
         * 新流程中第一步是开启 QBE 流程，内置调用 HQBE 接口，主要在工作流中使用
         * xxxx ? QBE = xxxxxx
         */
        return OAmbit.of(AmbitQBE.class).then(context, envelop)


            /*
             * Auditor 流程，针对特殊的字段启用默认提取
             * 1. 创建：createdAt, createdBy
             * 2. 更新：updatedAt, updatedBy
             */
            .compose(processed -> OAmbit.of(AmbitAuditor.class).then(context, processed))


            /*
             * DataRegion 前置处理
             * 1. 仅支持 POST / PUT 方法
             * 2. 查询引擎语法
             * {
             *     criteria: {},
             *     sorter: [],
             *     projection: [],
             *     pager:{
             *         page: xx,
             *         size: xx
             *     }
             * }
             * 3. criteria / projection 可能被更改
             * 4. 结果会被修正
             */
            .compose(processed -> OAmbit.of(AmbitRegionBefore.class).then(context, processed));
    }
}
