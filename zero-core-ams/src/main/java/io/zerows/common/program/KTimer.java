package io.zerows.common.program;

import io.zerows.ams.util.HUt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lang : 2023-06-11
 */
public class KTimer {

    private final transient String name;
    private transient long start;
    private transient long end;

    private KTimer(final String name) {
        this.name = name;
    }

    public static KTimer of() {
        return new KTimer(Thread.currentThread().getName());
    }

    public static KTimer of(final String name) {
        return new KTimer(name);
    }

    public KTimer start(final long start) {
        // Clean the queue
        this.start = start;
        return this;
    }

    public KTimer start() {
        this.start = System.currentTimeMillis();
        return this;
    }

    public KTimer end(final long end) {
        this.end = end;
        return this;
    }

    public KTimer end() {
        this.end = System.currentTimeMillis();
        return this;
    }

    public String name() {
        return this.name;
    }

    public String value() {
        final long duration = this.end - this.start;
        // toMills
        final LocalDateTime datetime = HUt.toDuration(duration);
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return format.format(datetime);
    }
}
