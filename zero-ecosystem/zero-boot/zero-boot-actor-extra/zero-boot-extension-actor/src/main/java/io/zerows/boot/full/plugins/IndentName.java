package io.zerows.boot.full.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.constant.OxConstant;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XCategory;
import io.zerows.program.Ux;
import io.zerows.spi.modeler.Identifier;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class IndentName implements Identifier {
    private final transient Identifier indent = Ut.singleton(IndentKey.class);

    @Override
    public Future<String> resolve(final JsonObject data, final JsonObject config) {
        return this.resolveDict(data, config).compose(processed -> this.indent.resolve(processed, config));
    }

    @Override
    public Future<ConcurrentMap<String, JsonArray>> resolve(final JsonObject data, final String identifier, final JsonObject config) {
        return this.resolveDict(data, config).compose(processed -> this.indent.resolve(processed, identifier, config));
    }

    private Future<JsonObject> resolveDict(final JsonObject input, final JsonObject config) {
        // Fetch map data from stored
        return this.sourceMap().compose(map -> {
            final JsonObject inputCopy = input.copy();
            final Object data = inputCopy.getValue(KName.DATA);
            if (data instanceof JsonObject) {
                final JsonObject jsonRef = (JsonObject) data;
                this.resolveData(jsonRef, map, config);
                inputCopy.put(KName.DATA, jsonRef);
            } else if (data instanceof JsonArray) {
                final JsonArray jsonRef = (JsonArray) data;
                Ut.itJArray(jsonRef).forEach(json -> this.resolveData(json, map, config));
                inputCopy.put(KName.DATA, jsonRef);
            }
            return Ux.future(inputCopy);
        });
    }

    private Future<ConcurrentMap<String, String>> sourceMap() {
        final JsonObject condition = new JsonObject();
        condition.put(KName.TYPE, "ci.type");
        return DB.on(XCategoryDao.class).<XCategory>fetchAndAsync(condition)
            .compose(categories -> Ux.future(Ut.elementMap(categories, XCategory::getName, XCategory::getKey)));
    }

    private void resolveData(final JsonObject data, final ConcurrentMap<String, String> dict, final JsonObject config) {
        final String first = config.getString(OxConstant.Field.DIM_1, OxConstant.Field.CATEGORY_FIRST);
        final String second = config.getString(OxConstant.Field.DIM_2, OxConstant.Field.CATEGORY_SECOND);
        final String third = config.getString(OxConstant.Field.DIM_3, OxConstant.Field.CATEGORY_THIRD);
        this.resolveData(data, dict, first);
        this.resolveData(data, dict, second);
        this.resolveData(data, dict, third);
    }

    private void resolveData(final JsonObject data, final ConcurrentMap<String, String> dict, final String field) {
        if (Ut.isNotNil(field) && data.containsKey(field)) {
            // Replace the field findRunning with input source here.
            final String replaced = dict.get(data.getString(field));
            if (Objects.nonNull(replaced)) {
                data.put(field, replaced);
            }
        }
    }
}
