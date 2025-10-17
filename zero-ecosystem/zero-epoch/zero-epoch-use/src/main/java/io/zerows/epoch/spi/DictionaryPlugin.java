package io.zerows.epoch.spi;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KDictConfig;

public interface DictionaryPlugin {

    default DictionaryPlugin configuration(final JsonObject configuration) {
        return this;
    }

    Future<JsonArray> fetchAsync(KDictConfig.Source source, MultiMap paramMap);

    default JsonArray fetch(final KDictConfig.Source source, final MultiMap paramMap) {
        return new JsonArray();
    }
}
