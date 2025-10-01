package io.zerows.epoch.component.serialization;

import io.zerows.epoch.program.Ut;

import java.util.Calendar;
import java.util.Date;

/**
 * Date, Calendar
 */
class DateSaber extends AbstractSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        if (Date.class == paramType ||
            Calendar.class == paramType) {
            this.verifyInput(!Ut.isDate(literal), paramType, literal);
            final Date reference = Ut.parse(literal);
            if (Calendar.class == paramType) {
                // Specific date formatFail
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(reference);
                return calendar;
            }
            return reference;
        }
        return new Date();
    }

    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (input instanceof final Date date) {
            reference = date.getTime();
        } else if (input instanceof final Calendar date) {
            reference = date.getTime().getTime();
        }
        return reference;
    }
}
