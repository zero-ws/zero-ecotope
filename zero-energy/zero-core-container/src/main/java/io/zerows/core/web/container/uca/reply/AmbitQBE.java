package io.zerows.core.web.container.uca.reply;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.core.web.io.zdk.qbe.HQBE;
import io.zerows.core.web.model.commune.Envelop;

/**
 * @author lang : 2024-06-27
 */
public class AmbitQBE implements OAmbit {
    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        /*
         * HQBE 新流程，流程执行结果如
         * HQBE -> ServiceLoader -> HQBEView -> HQR 或标准流程
         */
        final String qbe = context.request().getParam(KName.QBE);
        final HttpMethod method = context.request().method();
        if (Ut.isNil(qbe) || HttpMethod.POST != method) {
            /*
             * GET / PUT 跳过
             */
            return Future.succeededFuture(envelop);
        }
        return Ux.channel(HQBE.class, () -> envelop, hqbe -> {
            /*
             * 1. 先做Base64的解码
             * 2. 再根据解码结果隐式替换 Envelop 中的 criteria 部分，QR 专用
             */
            final JsonObject qbeJ = Ut.toJObject(Ut.decryptBase64(qbe));
            return hqbe.before(qbeJ, envelop);
        });
    }
}
