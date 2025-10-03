package io.zerows.weaver;

import io.zerows.support.Ut;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

class SaberJava8DataTime extends SaberBase {
    @Override
    public <T> Object from(final T input) {
        Object reference = null;
        if (input instanceof final LocalDate date) {
            reference = date.toString();
        } else if (input instanceof final LocalDateTime dateTime) {
            reference = dateTime.toString();
        } else if (input instanceof final LocalTime time) {
            reference = time.toString();
        }
        return reference;
    }

    @Override
    public Object from(final Class<?> paramType, final String literal) {
        if (Date.class == paramType ||
            Calendar.class == paramType) {
            this.verifyInput(!Ut.isDate(literal), paramType, literal);
            return Ut.parse(literal);
        }
        return new Date();
    }
}
