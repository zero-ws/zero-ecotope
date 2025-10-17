package io.zerows.program.feature;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KMap;
import io.zerows.support.Ut;

/*
 * Dual Processing for
 * ActIn / ActOut
 */
public interface Mapper {
    /*
     * Mapping
     * to -> from
     */
    JsonObject in(JsonObject in, KMap.Node mapping);

    default JsonArray in(final JsonArray in, final KMap.Node mapping) {
        final JsonArray normalized = new JsonArray();
        Ut.itJArray(in).map(each -> this.in(each, mapping)).forEach(normalized::add);
        return normalized;
    }

    /*
     * Mapping
     * from -> to
     */
    JsonObject out(JsonObject out, KMap.Node mapping);

    default JsonArray out(final JsonArray out, final KMap.Node mapping) {
        final JsonArray normalized = new JsonArray();
        Ut.itJArray(out).map(each -> this.out(each, mapping)).forEach(normalized::add);
        return normalized;
    }
}
