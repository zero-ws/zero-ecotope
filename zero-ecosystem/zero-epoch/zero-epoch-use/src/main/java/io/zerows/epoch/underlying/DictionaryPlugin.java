package io.zerows.epoch.underlying;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.metadata.datamation.KDictSource;

public interface DictionaryPlugin {

    default DictionaryPlugin configuration(final JsonObject configuration) {
        return this;
    }

    Future<JsonArray> fetchAsync(KDictSource source, MultiMap paramMap);

    default JsonArray fetch(final KDictSource source, final MultiMap paramMap) {
        return new JsonArray();
    }
}
