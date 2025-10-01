package io.zerows.extension.mbse.basement.uca.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.corpus.Ux;

import java.util.function.Function;

class InitModeler implements AoModeler {

    private static final Annal LOGGER = Annal.get(InitModeler.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return entityJson -> {
            LOGGER.debug("[ Ox ] 1. AoModeler.init() ：{0}", entityJson.encode());
            final JsonObject modelJson = new JsonObject();
            modelJson.put(KName.MODEL, entityJson);
            return Ux.future(modelJson);
        };
    }

    @Override
    public JsonObject executor(final JsonObject entityJson) {
        LOGGER.debug("[ Ox ] (Sync) 1. AoModeler.init() ：{0}", entityJson.encode());
        final JsonObject modelJson = new JsonObject();
        modelJson.put(KName.MODEL, entityJson);
        return modelJson;
    }
}