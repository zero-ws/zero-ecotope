package io.zerows.plugins.excel.component;

import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

public class ExBoundCol implements ExBound {
    private transient int start;
    private transient int end;

    public ExBoundCol(final Row row) {
        this(row.getFirstCellNum(), row.getLastCellNum());
    }

    public ExBoundCol(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExBoundCol)) {
            return false;
        }
        final ExBoundCol colBound = (ExBoundCol) o;
        return this.start == colBound.start &&
            this.end == colBound.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end);
    }

    @Override
    public String toString() {
        return "ColBound{" +
            "begin=" + this.start +
            ", end=" + this.end +
            '}';
    }

    @Override
    public int start() {
        return this.start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    @Override
    public int end() {
        return this.end;
    }

    public void setEnd(final int end) {
        this.end = end;
    }
}
