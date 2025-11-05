package io.zerows.extension.module.finance.common;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.finance.domain.tables.pojos.FPreAuthorize;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

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

    static JsonObject toTransaction(final JsonObject response, final List<DataTran> tranData) {
        final JsonArray transactions = new JsonArray();
        tranData.stream()
            .map(DataTran::toJson)
            .forEach(transactions::add);
        response.put(KName.Finance.TRANSACTIONS, transactions);
        return response;
    }
}
