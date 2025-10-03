package io.zerows.extension.mbse.basement.uca.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.component.log.Annal;
import io.zerows.extension.mbse.basement.domain.tables.daos.MKeyDao;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MKey;
import io.zerows.epoch.corpus.Ux;

import java.util.List;
import java.util.function.Function;

class KeyModeler implements AoModeler {
    private static final Annal LOGGER = Annal.get(FieldModeler.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return schemaJson -> {
            LOGGER.debug("[ Ox ] 6.2. AoModeler.key() ：{0}", schemaJson.encode());
            final JsonObject entityJson = AoModeler.getEntity(schemaJson);
            // 读取所有的Keys
            return Ux.Jooq.on(MKeyDao.class)
                .<MKey>fetchAndAsync(this.onCriteria(entityJson))
                .compose(Ux::futureA)
                .compose(keys -> Ux.future(this.onResult(schemaJson, keys)));
        };
    }

    @Override
    public JsonObject executor(final JsonObject schemaJson) {
        LOGGER.debug("[ Ox ] (Sync) 6.2. AoModeler.key() ：{0}", schemaJson.encode());
        final JsonObject entityJson = AoModeler.getEntity(schemaJson);
        // List
        final List<MKey> keyList = Ux.Jooq.on(MKeyDao.class)
            .fetchAnd(this.onCriteria(entityJson));
        // Array
        final JsonArray keys = Ux.toJson(keyList);
        return this.onResult(schemaJson, keys);
    }

    private JsonObject onResult(final JsonObject schemaJson,
                                final JsonArray keys) {
        return schemaJson.put(KName.Modeling.KEYS, keys);
    }

    private JsonObject onCriteria(final JsonObject entityJson) {
        final JsonObject filters = new JsonObject();
        filters.put(KName.ENTITY_ID, entityJson.getString(KName.KEY));
        return filters;
    }
}
