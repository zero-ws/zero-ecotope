package io.zerows.mbse.metadata;

import io.r2mo.spi.SPI;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Ut;

import java.util.Objects;

class Act {

    static ActOut empty() {
        return new ActOut(new JsonObject(), SPI.V_STATUS.ok204());
    }

    static ActOut response(final Boolean result) {
        return new ActOut(result);
    }

    static ActOut response(final Buffer buffer) {
        return new ActOut(buffer);
    }

    static ActOut response(final Throwable ex) {
        return new ActOut(ex);
    }

    static ActOut response(final JsonObject data) {
        if (Objects.isNull(data)) {
            return empty();
        } else {
            return new ActOut(data);
        }
    }

    static ActOut response(final JsonArray data) {
        if (Objects.isNull(data)) {
            return empty();
        } else {
            return new ActOut(data);
        }
    }

    static ActOut response(final HRecord[] records) {
        final JsonArray result = Ut.toJArray(records);
        return response(result);
    }

    static ActOut response(final HRecord record) {
        if (Objects.isNull(record)) {
            return empty();
        } else {
            return new ActOut(record.toJson());
        }
    }
}
