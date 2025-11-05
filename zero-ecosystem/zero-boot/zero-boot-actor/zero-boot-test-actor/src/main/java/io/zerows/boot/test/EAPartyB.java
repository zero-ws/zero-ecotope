package io.zerows.boot.test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.spi.DictionaryAmbient;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KIntegration;
import io.zerows.platform.metadata.KMap;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author lang : 2023-06-11
 */
class EAPartyB {

    static KDictConfig partyDict(final KIntegration integration) {
        final String dict = EAIo.ioPartyB("dict-config", integration);
        final String epsilon = EAIo.ioPartyB("dict-epsilon", integration);
        final JsonArray dictA = Ut.ioJArray(dict);
        final JsonObject useJ = Ut.ioJObject(epsilon);
        return new KDictConfig(dictA)
            .bind(Ux.dictUse(useJ))
            .bind(DictionaryAmbient.class);
    }

    static KMap partyMap(final KIntegration integration) {
        final String mapping = EAIo.ioPartyB(KName.MAPPING, integration);
        final JsonObject mapJ = Ut.ioJObject(mapping);
        return new KMap(mapJ);
    }

    static JsonObject partyOption(final KIntegration integration) {
        final String options = EAIo.ioPartyB(KName.OPTIONS, integration);
        return Ut.ioJObject(options);
    }
}
