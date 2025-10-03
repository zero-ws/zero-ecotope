package io.zerows.epoch.database.jooq.operation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class JoinUnique {
    private final transient JoinStore store;

    JoinUnique(final JoinStore store) {
        this.store = store;
    }

    // Unique
    Future<JsonObject> fetchById(final String key, final boolean isASub, final String field) {
        final UxJooq jooq = this.store.jooq();
        return jooq.fetchJByIdAsync(key).compose(response -> {
            if (isASub) {
                return this.fetchA(response).compose(data -> {
                    response.put(field, data);
                    return Ut.future(response);
                });
            } else {
                return this.fetchJ(response).compose(data -> {
                    response.put(field, data);
                    return Ut.future(response);
                });
            }
        });
    }

    private Future<JsonArray> fetchA(final JsonObject response) {
        final UxJooq childJq = this.store.childJooq();
        if (Objects.nonNull(childJq)) {
            final JsonObject joined = this.store.dataJoin(response);
            return childJq.fetchJAsync(joined);
        } else {
            return Ut.futureA();
        }
    }

    private Future<JsonObject> fetchJ(final JsonObject response) {
        final UxJooq childJq = this.store.childJooq();
        if (Objects.nonNull(childJq)) {
            final JsonObject joined = this.store.dataJoin(response);
            return childJq.fetchJOneAsync(joined);
        } else {
            return Ut.futureJ();
        }
    }
}
