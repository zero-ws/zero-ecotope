package io.zerows.extension.commerce.finance.uca.enter;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.exception.web._80413Exception501NotImplement;

import java.util.List;

/**
 * @author lang : 2024-01-19
 */
interface MakerOn<H, T> {

    default Future<T> buildAsync(final JsonObject data, final H assist) {
        throw new _80413Exception501NotImplement();
    }

    default Future<List<T>> buildAsync(final JsonArray data, final H assist) {
        throw new _80413Exception501NotImplement();
    }
}
