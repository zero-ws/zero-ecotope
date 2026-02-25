package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.modulat.domain.tables.pojos.BBag;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class CombinerDao implements Combiner<JsonObject, BBag> {
    @Override
    public Future<JsonObject> configure(final JsonObject response, final BBag bag) {
        final JsonObject uiConfig = Ut.toJObject(bag.getUiConfig());
        final String store = bag.getStore(); // uiConfig.getString(KName.STORE, null);
        if (Ut.isNil(store)) {
            return Ux.future(response);
        }

        final JsonObject record = Ut.valueJObject(uiConfig, KName.RECORD);
        if (Ut.isNil(record)) {
            return Ux.future(response);
        } else {
            final JsonObject parameters = new JsonObject();
            parameters.put(KName.APP_ID, bag.getAppId());
            parameters.put(KName.SIGMA, bag.getSigma());
            return Ke.umJData(record, parameters);
        }
    }
}
