package io.zerows.core.database.jooq.condition.date;

import io.zerows.core.database.jooq.condition.Term;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

@SuppressWarnings("all")
public class TermDateNull implements Term {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        return DSL.field(fieldName).isNull();
    }
}
