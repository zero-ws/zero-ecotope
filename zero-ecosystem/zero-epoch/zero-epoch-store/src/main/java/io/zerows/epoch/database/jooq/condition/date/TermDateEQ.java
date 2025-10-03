package io.zerows.epoch.database.jooq.condition.date;

import io.zerows.epoch.database.jooq.condition.Term;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

@SuppressWarnings("all")
public class TermDateEQ extends AbstractDateTerm {

    private final Term dayTerm = new InTermDateDay();

    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        return this.toDate(field,
            () -> dayTerm.where(field, fieldName, value),
            () -> DSL.field(fieldName).eq(value)
        );
    }
}
