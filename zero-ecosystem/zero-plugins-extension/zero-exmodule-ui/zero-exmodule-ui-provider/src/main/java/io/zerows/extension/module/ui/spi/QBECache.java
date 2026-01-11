package io.zerows.extension.module.ui.spi;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.extension.module.ui.domain.tables.pojos.UiView;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import io.zerows.specification.vital.HQR;
import io.zerows.support.Fx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * HQBE Cache 防止多次查询，此处查询比较频繁
 */
public class QBECache {
    public static final Cc<String, HQR> CCT_H_COND = Cc.openThread();
    private static final HMM<String, UiView> MM_QBE = HMM.<String, UiView>of(UiConstant.POOL_LIST_QR);

    public static Future<List<UiView>> cached(final List<UiView> listQr) {
        final List<Future<Boolean>> futures = new ArrayList<>();
        listQr.forEach(qr -> {
            // <sigma> / <code> / <name>
            final String key = qr.getSigma() + VString.SLASH +
                qr.getCode() + VString.SLASH +
                qr.getName();
            futures.add(MM_QBE.put(key, qr, 600).compose(v -> Ux.futureT()));
        });
        return Fx.combineT(futures).compose(done -> Ux.future(listQr));
    }

    public static Future<UiView> cached(final JsonObject qr, final Supplier<Future<UiView>> executor) {
        final String key = qr.getString(KName.SIGMA) + VString.SLASH +
            qr.getString(KName.CODE) + VString.SLASH +
            qr.getString(KName.NAME);
        return MM_QBE.cached(key, executor, 600);
    }
}
