package io.zerows.extension.commerce.finance.uca.enter;

import io.zerows.core.exception.web._501NotImplementException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author lang : 2024-01-19
 */
interface MakerOn<H, T> {

    default Future<T> buildAsync(final JsonObject data, final H assist) {
        throw new _501NotImplementException(this.getClass());
    }

    default Future<List<T>> buildAsync(final JsonArray data, final H assist) {
        throw new _501NotImplementException(this.getClass());
    }
}
