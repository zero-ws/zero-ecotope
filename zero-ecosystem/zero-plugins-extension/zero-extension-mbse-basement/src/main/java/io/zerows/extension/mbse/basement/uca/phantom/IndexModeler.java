package io.zerows.extension.mbse.basement.uca.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.basement.domain.tables.daos.MIndexDao;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MIndex;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;

import java.util.List;
import java.util.function.Function;

class IndexModeler implements AoModeler {
    private static final LogOf LOGGER = LogOf.get(FieldModeler.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return schemaJson -> {
            LOGGER.debug("[ Ox ] 6.3. AoModeler.index() ：{0}", schemaJson.encode());
            final JsonObject entityJson = AoModeler.getEntity(schemaJson);
            // 读取所有的indexes
            return DB.on(MIndexDao.class)
                .<MIndex>fetchAndAsync(this.onCriteria(entityJson))
                .compose(Ux::futureA)
                .compose(indexes -> Ux.future(this.onResult(schemaJson, indexes)));
        };
    }

    @Override
    public JsonObject executor(final JsonObject schemaJson) {
        LOGGER.debug("[ Ox ] (Sync) 6.3. AoModeler.index() ：{0}", schemaJson.encode());
        final JsonObject entityJson = AoModeler.getEntity(schemaJson);
        // List
        final List<MIndex> indexList = DB.on(MIndexDao.class)
            .fetchAnd(this.onCriteria(entityJson));
        // Array
        final JsonArray indexes = Ux.toJson(indexList);

        return this.onResult(schemaJson, indexes);
    }

    private JsonObject onResult(final JsonObject schemaJson,
                                final JsonArray indexes) {
        return schemaJson.put(KName.Modeling.INDEXES, indexes);
    }

    private JsonObject onCriteria(final JsonObject entityJson) {
        final JsonObject filters = new JsonObject();
        filters.put(KName.ENTITY_ID, entityJson.getString(KName.KEY));
        return filters;
    }
}
