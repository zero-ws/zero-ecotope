package io.zerows.boot.test.metadata;

public class QLevel {
    private final Integer step;
    private Integer start = 0;

    public QLevel(final Integer step) {
        this.step = step;
    }

    public void moveOn() {
        this.start = this.start + this.step;
        // return this;
    }

    public Integer value() {
        return this.start;
    }

    public void jump(final Integer start) {
        this.start = start;
        // return this;
    }
}
