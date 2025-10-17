package io.zerows.cosmic.plugins.job;

import io.zerows.platform.constant.VString;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.platform.metadata.Kv;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class JobAtBase implements JobAt {
    @Override
    public Queue<Instant> analyze(final List<String> formulas, final Instant instant) {
        final List<Instant> parsedList = new ArrayList<>();
        formulas.forEach(formula -> {
            final String[] segments = formula.split(VString.SLASH);
            if (1 <= segments.length) {
                String tmpTime = segments[0];
                if (4 == tmpTime.length()) { // like 3:00, 9:00
                    tmpTime = "0" + tmpTime;
                }
                final LocalTime time = Ut.toTime(tmpTime);
                // Extract Segment of Part 2
                final String segment;
                if (1 == segments.length) {
                    segment = null;
                } else {
                    segment = segments[1];
                }
                /*
                 * Child Analyzing Based on:
                 * 1. LocalTime
                 * 2. Segment
                 * 3. The TimeStamp Of Each Start
                 */
                final LocalDateTime startAt = Ut.toDateTime(instant);
                final LocalDateTime parsedAt = this.analyze(startAt, time, segment);
                if (Objects.nonNull(parsedAt)) {
                    final Instant parsed = Ut.parse(parsedAt).toInstant();
                    if (parsed.isAfter(instant)) {
                        /*
                         * parsed > instant ( Valid )
                         **/
                        parsedList.add(parsed);
                    }
                }
            } else {
                final Logger log = LoggerFactory.getLogger(this.getClass());
                log.warn("[ ZERO ] 任务表达式无法解析: {}", formula);
            }
        });
        // Instant from `past -> now -> future`
        parsedList.sort(Instant::compareTo);
        return new ConcurrentLinkedDeque<>(parsedList);
    }

    /*
     * When the duration time is greater than 1 day:
     * yyyy - years
     * MM   - months
     * dd   - days
     *
     * The left part is
     * HH   - hours
     * mm   - minutes
     * ss   - seconds
     * SSS  - mill-seconds
     */

    protected LocalDateTime analyze(final LocalDateTime startAt, final LocalTime time, final String segment) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    private Kv<Integer, Integer> parseSegment(final String segment) {
        if (Ut.isNotNil(segment) && segment.contains(VString.DASH)) {
            try {
                final String[] split = segment.split(VString.DASH);
                final Integer m = Integer.parseInt(split[0]);
                final Integer d = Integer.parseInt(split[1]);
                return Kv.create(m, d);
            } catch (final Throwable ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    protected LocalDateTime plusWith(final LocalDateTime find, final String segment) {
        final Kv<Integer, Integer> md = this.parseSegment(segment);// Calculate the New Day
        if (Objects.isNull(md)) {
            final int dayAdjust = Integer.parseInt(segment);
            return find.plusDays(dayAdjust - 1);
        } else {
            return find.plusMonths(md.key() - 1)
                .plusDays(md.value() - 1);
        }
    }
}
