package io.zerows.epoch.database.jooq.condition.date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;

/**
 * Field Format: "field,day"
 *
 * @author lang : 2024-10-21
 */
@SuppressWarnings("all")
public class InTermDateDay extends AbstractDateTerm {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        final LocalDate date = this.toDate(value);
        //  field.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        //  New Condition to replace between
        final Condition min = DSL.field(fieldName).ge(date.atStartOfDay());
        final Condition max = DSL.field(fieldName).lt(date.plusDays(1).atStartOfDay());
        return min.and(max);
    }
}
