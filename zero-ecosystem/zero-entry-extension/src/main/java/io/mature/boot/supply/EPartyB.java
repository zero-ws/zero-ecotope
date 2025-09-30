package io.mature.boot.supply;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.common.app.KIntegration;
import io.zerows.common.datamation.KDictConfig;
import io.zerows.common.datamation.KMap;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.ambient.osgi.spi.component.ExAmbientDictionary;
import io.zerows.unity.Ux;

/**
 * @author lang : 2023-06-11
 */
class EPartyB {

    static KDictConfig partyDict(final KIntegration integration) {
        final String dict = EIo.ioPartyB("dict-config", integration);
        final String epsilon = EIo.ioPartyB("dict-epsilon", integration);
        final JsonArray dictA = Ut.ioJArray(dict);
        final JsonObject useJ = Ut.ioJObject(epsilon);
        return new KDictConfig(dictA)
            .bind(Ux.dictUse(useJ))
            .bind(ExAmbientDictionary.class);
    }

    static KMap partyMap(final KIntegration integration) {
        final String mapping = EIo.ioPartyB(KName.MAPPING, integration);
        final JsonObject mapJ = Ut.ioJObject(mapping);
        return new KMap(mapJ);
    }

    static JsonObject partyOption(final KIntegration integration) {
        final String options = EIo.ioPartyB(KName.OPTIONS, integration);
        return Ut.ioJObject(options);
    }
}
