package io.zerows.extension.sdk.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.serviceimpl.DatumService;
import io.zerows.extension.module.ambient.servicespec.DatumStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 字典快速处理
 *
 * @author lang : 2025-12-15
 */
class UxExDatum {

    private static final DatumStub stub = Ut.singleton(DatumService.class);

    static Future<ConcurrentMap<String, JsonArray>> dictData(final String sigma, final Set<String> types) {
        return stub.dictSigma(sigma, Ut.toJArray(types))
            .compose(result -> Ux.future(Ut.elementGroup(result, KName.TYPE)));
    }

    static void dictGet(final JsonArray source, final String code, final Consumer<String> consumer) {
        final String key = dictGet(source, code);
        if (Ut.isNotNil(key)) {
            consumer.accept(key);
        }
    }

    static String dictGet(final JsonArray source, final String code) {
        if (Ut.isNotNil(source) && Ut.isNotNil(code)) {
            return Ut.itJArray(source)
                .filter(item -> code.equals(item.getValue(KName.CODE)))
                .map(item -> item.getString(KName.KEY))
                .findAny().orElse(null);
        } else {
            return null;
        }
    }
}
