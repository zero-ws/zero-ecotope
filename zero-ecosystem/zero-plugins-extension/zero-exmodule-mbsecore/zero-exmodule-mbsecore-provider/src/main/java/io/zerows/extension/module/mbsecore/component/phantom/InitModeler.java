package io.zerows.extension.module.mbsecore.component.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.program.Ux;

import java.util.function.Function;

class InitModeler implements AoModeler {

    private static final LogOf LOGGER = LogOf.get(InitModeler.class);

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