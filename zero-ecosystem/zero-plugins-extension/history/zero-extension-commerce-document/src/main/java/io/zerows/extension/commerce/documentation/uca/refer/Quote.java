package io.zerows.extension.commerce.documentation.uca.refer;

import io.zerows.core.exception.web._501NotImplementException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.documentation.eon.em.EmRefer;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 单向创建关联专用，包括读取对端数据专用，根据 {@link EmRefer.Entity}
 * 的值构造不同实体创建关联的内容。
 *
 * @author lang : 2023-09-25
 */
public interface Quote {

    static Quote of(final EmRefer.Entity fromType) {
        Objects.requireNonNull(fromType);
        final String type = fromType.name();

        final Supplier<Quote> supplier = POOL.SUPPLIER.get(fromType);
        Objects.requireNonNull(supplier);
        return CACHE.CCT_QUOTE.pick(supplier, type);
    }

    default Future<JsonArray> plugAsync(final JsonObject fromJ, final JsonArray toA,
                                        final EmRefer.Entity toType) {
        return Ut.Bnd.failOut(_501NotImplementException.class, this.getClass());
    }

    default Future<JsonArray> plugAsync(final JsonObject fromJ, final JsonObject toJ,
                                        final EmRefer.Entity toType) {
        return Ut.Bnd.failOut(_501NotImplementException.class, this.getClass());
    }

    default Future<JsonArray> fetchAsync(final String fromId, final EmRefer.Entity toType) {
        return Ux.futureA();
    }

    default Future<Boolean> removeAsync(final String fromId, final Set<String> keys,
                                        final EmRefer.Entity toType) {
        return Ux.futureT();
    }
}
