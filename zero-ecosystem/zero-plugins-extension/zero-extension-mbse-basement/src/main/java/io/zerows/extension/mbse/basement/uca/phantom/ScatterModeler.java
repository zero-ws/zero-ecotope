package io.zerows.extension.mbse.basement.uca.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.Annal;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.program.fn.Fx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class ScatterModeler implements AoModeler {
    private static final Annal LOGGER = Annal.get(ScatterModeler.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return modelJson -> {
            LOGGER.debug("[ Ox ] 5. AoModeler.scatter() ：{0}", modelJson.encode());
            // schemata 处理
            final JsonArray schemata = AoModeler.getSchemata(modelJson);
            // 并行处理 Fields, Index, Keys
            final List<Future<JsonObject>> futures = new ArrayList<>();
            Ut.itJArray(schemata).forEach(schema -> futures.add(
                Ux.future(schema)
                    // Field
                    .compose(AoModeler.field().apply())
                    // Key
                    .compose(AoModeler.key().apply())
                    // Index
                    .compose(AoModeler.index().apply())
            ));
            return Fx.combineA(futures)
                .compose(schemataJson -> Ux.future(this.onResult(modelJson, schemataJson)));
        };
    }

    @Override
    public JsonObject executor(final JsonObject modelJson) {
        LOGGER.debug("[ Ox ] (Sync) 5. AoModeler.scatter() ：{0}", modelJson.encode());
        // schemata 处理
        final JsonArray schemata = AoModeler.getSchemata(modelJson);
        // 处理 Fields, Index, Keys
        Ut.itJArray(schemata).forEach(schema -> {
            // Fields,
            AoModeler.field().executor(schema);
            // Key
            AoModeler.key().executor(schema);
            // Index
            AoModeler.index().executor(schema);
        });
        return this.onResult(modelJson, schemata);
    }

    private JsonObject onResult(final JsonObject modelJson,
                                final JsonArray schemata) {
        return modelJson.put(KName.Modeling.SCHEMATA, schemata);
    }
}
