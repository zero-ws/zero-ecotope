package io.zerows.extension.commerce.finance.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.finance.atom.TranData;
import io.zerows.extension.commerce.finance.domain.tables.pojos.FPreAuthorize;

import java.util.List;

/**
 * @author lang : 2024-01-11
 */
final class FmCombine {
    private FmCombine() {
    }

    static FPreAuthorize toAuthorize(final JsonObject data) {
        final JsonObject preJ = Ut.valueJObject(data);
        final FPreAuthorize authorize;
        if (preJ.containsKey("preAuthorize")) {
            final JsonObject preAuthorize = data.getJsonObject("preAuthorize");
            final JsonObject authorizeJson = data.copy().mergeIn(preAuthorize);
            authorize = Ux.fromJson(authorizeJson, FPreAuthorize.class);
        } else {
            authorize = null;
        }
        return authorize;
    }

    static JsonObject toTransaction(final JsonObject response, final List<TranData> tranData) {
        final JsonArray transactions = new JsonArray();
        tranData.stream()
            .map(TranData::toJson)
            .forEach(transactions::add);
        response.put(KName.Finance.TRANSACTIONS, transactions);
        return response;
    }
}
