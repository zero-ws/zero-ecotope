package io.zerows.extension.mbse.ui.uca.qbe;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.constant.VString;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.web.cache.Rapid;
import io.zerows.support.FnBase;
import io.zerows.extension.mbse.ui.domain.tables.pojos.UiView;
import io.zerows.extension.mbse.ui.eon.UiConstant;
import io.zerows.specification.vital.HQR;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * HQBE Cache 防止多次查询，此处查询比较频繁
 */
public class QBECache {
    public static final Cc<String, HQR> CCT_H_COND = Cc.openThread();
    private static final Rapid<String, UiView> RAPID = Rapid.object(UiConstant.POOL_LIST_QR, 600); // 10 min

    public static Future<List<UiView>> cached(final List<UiView> listQr) {
        final List<Future<Boolean>> futures = new ArrayList<>();
        listQr.forEach(qr -> {
            // <sigma> / <code> / <name>
            final String key = qr.getSigma() + VString.SLASH +
                qr.getCode() + VString.SLASH +
                qr.getName();
            futures.add(RAPID.write(key, qr).compose(v -> Ux.futureT()));
        });
        return FnBase.combineT(futures).compose(done -> Ux.future(listQr));
    }

    public static Future<UiView> cached(final JsonObject qr, final Supplier<Future<UiView>> executor) {
        final String key = qr.getString(KName.SIGMA) + VString.SLASH +
            qr.getString(KName.CODE) + VString.SLASH +
            qr.getString(KName.NAME);
        return RAPID.cached(key, executor);
    }
}
