package io.zerows.cosmic.webflow;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.HQBE;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-06-27
 */
public class AmbitQBE implements Ambit {
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
        return HPI.of(HQBE.class).waitAsync(hqbe -> {
            /*
             * 1. 先做Base64的解码
             * 2. 再根据解码结果隐式替换 Envelop 中的 criteria 部分，QR 专用
             */
            final JsonObject qbeJ = Ut.toJObject(Ut.decryptBase64(qbe));
            return hqbe.before(qbeJ, envelop);
        }, () -> envelop);
    }
}
