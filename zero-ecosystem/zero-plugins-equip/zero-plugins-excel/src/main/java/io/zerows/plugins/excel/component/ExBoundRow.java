package io.zerows.plugins.excel.component;

import org.apache.poi.ss.usermodel.Sheet;
import org.jspecify.annotations.NonNull;

public record ExBoundRow(int start, int end) implements ExBound {
    public ExBoundRow(final Sheet sheet) {
        this(sheet.getFirstRowNum(), sheet.getLastRowNum());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ExBoundRow rowBound)) {
            return false;
        }
        return this.start == rowBound.start &&
            this.end == rowBound.end;
    }

    @Override
    public @NonNull String toString() {
        return "RowBound{" +
            "begin=" + this.start +
            ", end=" + this.end +
            '}';
    }
}
