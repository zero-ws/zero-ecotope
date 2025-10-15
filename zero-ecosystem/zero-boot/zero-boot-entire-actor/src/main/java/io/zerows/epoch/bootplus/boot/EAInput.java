package io.zerows.epoch.bootplus.boot;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.stellar.OkA;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.KGlobal;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;

import java.util.Objects;

/**
 * @author lang : 2023-06-11
 */
class EAInput {

    static MultiMap input(final OkA partyA, final String identifier) {
        final MultiMap params = MultiMap.caseInsensitiveMultiMap();
        params.add(KName.IDENTIFIER, identifier);
        /*
         * 应用处理
         */
        final JsonObject data = inputQr(partyA);
        params.add(KName.SIGMA, data.getString(KName.SIGMA));
        params.add(KName.APP_ID, data.getString(KName.APP_ID));
        return params;
    }

    static JsonObject inputQr(final OkA partyA) {
        final JsonObject params = new JsonObject();
        final HArk ark = partyA.configArk();
        if (Objects.isNull(ark)) {
            final KGlobal tenant = partyA.partyA();
            final JsonObject application = tenant.getApplication();
            params.put(KName.SIGMA, application.getString(KName.SIGMA));
            params.put(KName.APP_ID, application.getString(KName.APP_ID));
        } else {
            final HApp app = ark.app();
            params.put(KName.APP_ID, app.option(KName.APP_ID));
            params.put(KName.SIGMA, app.option(KName.SIGMA));
        }
        return params;
    }
}
