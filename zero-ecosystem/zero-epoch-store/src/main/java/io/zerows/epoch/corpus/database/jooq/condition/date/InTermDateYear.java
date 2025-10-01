package io.zerows.epoch.corpus.database.jooq.condition.date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public class InTermDateYear extends AbstractDateTerm{
    @Override
    public Condition where(Field field, String fieldName, Object value) {
        final LocalDate date = this.toDate(value);
        int year = date.getYear();
        LocalDate startOfYear = LocalDate.of(year, Month.JANUARY, 1);  // 1月1日
        LocalDate endOfYear = LocalDate.of(year, Month.DECEMBER, 31);  // 12月31日

        // 转换为 LocalDateTime
        LocalDateTime startOfDay = startOfYear.atStartOfDay(); // 1月1日 00:00:00
        LocalDateTime endOfDay = endOfYear.atTime(23, 59, 59, 999999999); // 12月31日 23:59:59.999999999

        // 构造Jooq的BETWEEN条件
        return DSL.condition("{0} BETWEEN {1} AND {2}", field, startOfDay, endOfDay);
    }
}
