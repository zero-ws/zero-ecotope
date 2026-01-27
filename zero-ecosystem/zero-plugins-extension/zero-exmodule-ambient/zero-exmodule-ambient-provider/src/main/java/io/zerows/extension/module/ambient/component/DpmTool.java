package io.zerows.extension.module.ambient.component;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.EmDS;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class DpmTool {

    static final ConcurrentMap<EmDS.Dictionary, Dpm> POOL_DPM = new ConcurrentHashMap<EmDS.Dictionary, Dpm>() {
        {
            this.put(EmDS.Dictionary.ASSIST, Ut.instance(DpmAssist.class));
            this.put(EmDS.Dictionary.CATEGORY, Ut.instance(DpmCategory.class));
            this.put(EmDS.Dictionary.TABULAR, Ut.instance(DpmTabular.class));
            this.put(EmDS.Dictionary.DAO, Ut.instance(DpmDao.class));
        }
    };

    /**
     * Build condition for `X_CATEGORY, X_TABULAR` etc.
     *
     * @param params  {@link MultiMap} The parameters map that came from vert.x
     * @param typeSet {@link Set<String>} The definition of dict source.
     * @return {@link JsonObject} Return to json data with criteria formatFail
     */
    static JsonObject condition(final MultiMap params, final Set<String> typeSet) {
        /* Result */
        final JsonObject condition = new JsonObject();
        /* Sigma for each application */
        final String sigma = params.get(KName.SIGMA);
        condition.put(KName.SIGMA, sigma);

        /* Types */
        if (!typeSet.isEmpty()) {
            condition.put(KName.TYPE + ",i", Ut.toJArray(typeSet));
            condition.put(VString.EMPTY, Boolean.TRUE);
        }
        return condition;
    }
}
