package io.zerows.epoch.bootplus.boot.supply;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.component.shared.app.KIntegration;
import io.zerows.component.shared.datamation.KDictConfig;
import io.zerows.component.shared.datamation.KMap;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.ambient.osgi.spi.component.ExAmbientDictionary;

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
