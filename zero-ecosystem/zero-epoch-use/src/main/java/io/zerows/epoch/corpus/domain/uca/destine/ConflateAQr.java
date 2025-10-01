package io.zerows.epoch.corpus.domain.uca.destine;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.domain.atom.specification.KJoin;

/**
 * @author lang : 2023-08-01
 */
class ConflateAQr extends ConflateBase<JsonArray, JsonObject> {
    private final transient Conflate<JsonObject, JsonObject> conflate;

    protected ConflateAQr(final KJoin joinRef) {
        super(joinRef);
        this.conflate = new ConflateJQr(joinRef);
    }

    @Override
    public JsonObject treat(final JsonArray active, final String identifier) {
        final JsonObject condition = new JsonObject();
        Ut.itJArray(active, (json, index) -> {
            final JsonObject eachQr = this.conflate.treat(json, identifier);
            if (Ut.isNotNil(eachQr)) {
                condition.put("$" + index, eachQr);
            }
        });
        return condition;
    }
}
