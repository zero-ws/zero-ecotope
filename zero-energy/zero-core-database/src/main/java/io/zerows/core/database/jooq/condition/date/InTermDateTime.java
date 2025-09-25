package io.zerows.core.database.jooq.condition.date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.sql.Time;

public class InTermDateTime extends AbstractDateTerm{
    @Override
    public Condition where(Field field, String fieldName, Object value) {
        String[] split = value.toString().split(",");
        if(split.length==0){
            return DSL.noCondition();
        }
        Time time1 = Time.valueOf(split[0]);
        Time time2 = Time.valueOf(split[1]);
        int hour1 = time1.toLocalTime().getHour();
        int minute1 = time1.toLocalTime().getMinute();
        int hour2 = time2.toLocalTime().getHour();
        int minute2 = time2.toLocalTime().getMinute();
        Field<Integer> field1 = DSL.field("HOUR({0})", Integer.class, DSL.field(fieldName));
        Field<Integer> field2 = DSL.field("MINUTE({0})", Integer.class, DSL.field(fieldName));
        return DSL.condition(
                DSL.or(
                        DSL.and(field1.eq(hour1), field2.ge(minute1)),  // time1 起始部分
                        DSL.and(field1.gt(hour1), field1.lt(hour2)),    // 完全在中间部分
                        DSL.and(field1.eq(hour2), field2.le(minute2))   // time2 结束部分
                )
        );
    }
}
