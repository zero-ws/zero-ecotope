package io.zerows.extension.mbse.modulat.uca.configure;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.extension.mbse.modulat.domain.tables.pojos.BBag;
import io.zerows.unity.Ux;

import java.util.Collection;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class CombinerOutChildren implements Combiner<JsonObject, Collection<BBag>> {
    @Override
    public Future<JsonObject> configure(final JsonObject response, final Collection<BBag> map) {
        /*
         * Extract required fields
         * - name, nameAbbr, nameFull
         * - uiIcon, uiStyle, uiSort
         * - key
         *
         * {
         *      "bag": {
         *          "children": []
         *      }
         * }
         */
        JsonObject bagRef = response.getJsonObject(KName.App.BAG);
        if (Objects.isNull(bagRef)) {
            bagRef = new JsonObject();
            response.put(KName.App.BAG, bagRef);
        }
        final JsonArray children = new JsonArray();
        map.forEach(bag -> children.add(CombinerKit.normalize(bag)));
        bagRef.put(KName.CHILDREN, children);
        // Double Check for Ensure
        {
            response.put(KName.App.BAG, bagRef);
        }
        return Ux.future(response);
    }
}
