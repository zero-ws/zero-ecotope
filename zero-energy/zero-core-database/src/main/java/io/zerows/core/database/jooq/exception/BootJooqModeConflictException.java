package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;
import io.zerows.core.uca.qr.syntax.Ir;
import io.vertx.core.json.JsonObject;

public class BootJooqModeConflictException extends BootingException {

    public BootJooqModeConflictException(
        final Class<?> clazz,
        final Ir.Mode required,
        final JsonObject filters) {
        super(clazz, required, filters.encode());
    }

    @Override
    public int getCode() {
        return -40058;
    }
}
