package io.zerows.extension.mbse.basement.uca.phantom;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.basement.domain.tables.daos.MFieldDao;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MField;
import io.zerows.epoch.database.DB;
import io.zerows.program.Ux;

import java.util.List;
import java.util.function.Function;

class FieldModeler implements AoModeler {

    private static final LogOf LOGGER = LogOf.get(FieldModeler.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return schemaJson -> {
            LOGGER.debug("[ Ox ] 6.1. AoModeler.field() ：{0}", schemaJson.encode());
            // 读取所有的Fields
            final JsonObject entityJson = AoModeler.getEntity(schemaJson);
            return DB.on(MFieldDao.class)
                .<MField>fetchAndAsync(this.onCriteria(entityJson))
                .compose(Ux::futureA)
                .compose(fields -> Ux.future(this.onResult(schemaJson, fields)));
        };
    }

    @Override
    public JsonObject executor(final JsonObject schemaJson) {
        LOGGER.debug("[ Ox ] (Sync) 6.1. AoModeler.field() ：{0}", schemaJson.encode());
        final JsonObject entityJson = AoModeler.getEntity(schemaJson);
        // List
        final List<MField> fields = DB.on(MFieldDao.class)
            .fetchAnd(this.onCriteria(entityJson));
        // JsonArray
        final JsonArray fieldArr = Ux.toJson(fields);
        return this.onResult(schemaJson, fieldArr);
    }

    private JsonObject onResult(final JsonObject schemaJson,
                                final JsonArray fields) {
        return schemaJson.put(KName.Modeling.FIELDS, fields);
    }

    private JsonObject onCriteria(final JsonObject entityJson) {
        final JsonObject filters = new JsonObject();
        filters.put("entityId", entityJson.getString(KName.KEY));
        return filters;
    }
}