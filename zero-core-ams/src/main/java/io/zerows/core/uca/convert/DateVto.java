package io.zerows.core.uca.convert;

import io.zerows.ams.util.HUt;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;

public class DateVto implements Vto<Date> {

    @Override
    public Date to(final Object value, final Class<?> type) {
        if (Objects.isNull(value)) {
            return new Date();
        } else {
            if (Date.class == type) {
                return (Date) value;
            } else if (String.class == type) {
                /*
                 * Default Parsed
                 */
                return HUt.parseFull(value.toString());
            } else if (LocalTime.class == type) {
                return HUt.parseFull(value.toString());
            } else if (LocalDate.class == type) {
                return HUt.parseFull(value.toString());
            } else if (LocalDateTime.class == type) {
                return HUt.parseFull(value.toString());
            } else if (Instant.class == type) {
                return Date.from((Instant) value);
            }
        }
        return null;
    }
}
