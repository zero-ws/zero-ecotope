package io.zerows.cosmic.webflow;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.jigsaw.ZeroPlugins;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Fx;

import java.util.List;
import java.util.function.Function;

/**
 * @author lang : 2024-06-27
 */
public class AmbitNext implements Ambit {

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        /*
         * 新流程中第一步是开启 QBE 流程，内置调用 HQBE 接口，主要在工作流中使用
         * xxxx ? QBE = xxxxxx
         */
        return Ambit.of(AmbitQBE.class).then(context, envelop)


            /*
             * Auditor 流程，针对特殊的字段启用默认提取
             * 1. 创建：createdAt, createdBy
             * 2. 更新：updatedAt, updatedBy
             */

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
            .compose(processed -> this.beforeApply(context, processed));
    }


    private Future<Envelop> beforeApply(final RoutingContext context, final Envelop envelop) {
        final Vertx vertx = context.vertx();

        final List<UnderApply> underApply = ZeroPlugins.of(vertx).createPlugin(UnderApply.class);
        final List<Function<Envelop, Future<Envelop>>> underApplyFn = underApply.stream()
            .map(item -> (Function<Envelop, Future<Envelop>>) (envelopInput -> item.before(context, envelopInput)))
            .toList();
        return Fx.passion(envelop, underApplyFn);
    }
}
