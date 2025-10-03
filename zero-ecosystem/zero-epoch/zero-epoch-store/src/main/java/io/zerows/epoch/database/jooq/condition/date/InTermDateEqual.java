package io.zerows.epoch.database.jooq.condition.date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InTermDateEqual extends AbstractDateTerm {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        final LocalDate date = this.toDate(value);
        // 构造日期范围条件
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);
        return DSL.condition("{0} BETWEEN {1} AND {2}", field, startOfDay, endOfDay);
    }
}
