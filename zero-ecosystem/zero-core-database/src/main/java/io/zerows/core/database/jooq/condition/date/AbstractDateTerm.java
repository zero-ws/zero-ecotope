package io.zerows.core.database.jooq.condition.date;

import io.zerows.core.database.jooq.condition.Term;
import io.zerows.core.util.Ut;
import org.jooq.Condition;
import org.jooq.Field;

import java.time.LocalDate;
import java.util.Date;
import java.util.function.Supplier;

public abstract class AbstractDateTerm implements Term {

    protected LocalDate toDate(final Object value) {
        final Date normalized = Ut.parseFull(value.toString());
        return Ut.toDate(normalized.toInstant());
    }

    @SuppressWarnings("all")
    protected Condition toDate(final Field field,
                               final Supplier<Condition> dateSupplier, final Supplier<Condition> otherSupplier) {
        final Class<?> type = field.getType();
        if (LocalDate.class == type) { // 如果字段是 LocalDate
            return dateSupplier.get();
        } else { // 如果是其他类型
            return otherSupplier.get();
        }
    }
}
