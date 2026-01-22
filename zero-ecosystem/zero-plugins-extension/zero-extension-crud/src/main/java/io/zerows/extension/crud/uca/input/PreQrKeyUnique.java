package io.zerows.extension.crud.uca.input;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class PreQrKeyUnique implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        /* Unique Keys */
        final JsonArray unique = in.module().getField().getUnique();

        /* Each Unique */
        final JsonObject filters = this.condition(data, unique);
        log.info("{} 查询条件 By / {}", IxConstant.K_PREFIX, filters.encode());
        return Ux.future(filters);
    }

    private JsonObject condition(final JsonObject data, final JsonArray unique) {
        final JsonObject filters = new JsonObject();
        if (VValue.ONE == unique.size()) {
            final JsonArray fields = unique.getJsonArray(VValue.IDX);
            final Set<String> fieldSet = Ut.toSet(fields);
            filters.mergeIn(this.condition(data, fieldSet));
        } else {
            filters.put(VString.EMPTY, Boolean.FALSE);
            Ut.itJArray(unique, JsonArray.class,
                (each, index) -> filters.put("$" + index, this.condition(data, Ut.toSet(each))));
        }
        return filters;
    }

    private JsonObject condition(final JsonObject data, final Set<String> fieldSet) {
        final JsonObject filters = new JsonObject();
        filters.put(VString.EMPTY, Boolean.TRUE);
        fieldSet.forEach(field -> {
            final Object value = data.getValue(field);
            filters.put(field, value);
        });
        return filters;
    }

    @Override
    public Future<JsonObject> inAJAsync(final JsonArray array, final IxMod in) {
        /* Unique Keys */
        final JsonArray unique = in.module().getField().getUnique();
        final JsonObject filters = new JsonObject();
        Ut.itJArray(array, JsonObject.class, (data, index) -> {
            /* Each condition */
            final JsonObject cond = this.condition(data, unique);
            filters.put("$" + index, cond);
        });
        return Ux.future(filters);
    }
}
