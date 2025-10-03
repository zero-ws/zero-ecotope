package io.zerows.extension.runtime.report.refine;

import io.vertx.core.json.JsonObject;
import io.zerows.constant.VValue;
import io.zerows.epoch.program.Ut;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author lang : 2024-11-14
 */
class RpValue {

    private static Object formatTwo(final String[] modes) {
        final String mode = modes[VValue.IDX];
        final String pattern = modes[VValue.ONE];
        if ("TODAY".equals(mode)) {
            return LocalDate.now().atStartOfDay()
                .format(DateTimeFormatter.ofPattern(pattern));
        }

        if ("TOMORROW".equals(mode)) {
            return LocalDate.now().plusDays(1).atStartOfDay()
                .format(DateTimeFormatter.ofPattern(pattern));
        }

        if ("NOW".equals(mode)) {
            return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(pattern));
        }

        if ("NOW_TOMORROW".equals(mode)) {
            return LocalDateTime.now().plusDays(1)
                .format(DateTimeFormatter.ofPattern(pattern));
        }

        if ("TIME".equals(mode)) {
            return LocalTime.now()
                .format(DateTimeFormatter.ofPattern(pattern));
        }

        return null;
    }

    private static Object formatIn(final String[] modes, final JsonObject params) {
        final String inField = modes[VValue.IDX];
        final String inPattern = modes[VValue.ONE];

        final String valueDate = Ut.valueString(params, inField);
        final LocalDateTime valueAt = Ut.toDateTime(Ut.parseFull(valueDate));

        if (2 == modes.length) {
            return Ut.fromDate(valueAt, inPattern);
        } else {
            final int adjust = Integer.parseInt(modes[VValue.TWO]);
            if (0 < adjust) {
                return Ut.fromDate(valueAt.plusDays(adjust), inPattern);
            } else {
                return Ut.fromDate(valueAt.minusDays(adjust), inPattern);
            }
        }
    }

    static Object format(final String expression, final JsonObject params) {
        final String[] modes = expression.split(",");
        Object value = null;
        if (2 == modes.length) {
            value = formatTwo(modes);
        }
        if (Objects.nonNull(value)) {
            return value;
        }
        return formatIn(modes, params);
    }
}
