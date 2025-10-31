package io.zerows.plugins.excel.metadata;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class ExPos {
    private final transient int rowIndex;
    private final transient int colIndex;

    private ExPos(final int rowIndex, final int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public static ExPos index(final int rowIndex, final int colIndex) {
        return new ExPos(rowIndex, colIndex);
    }

    public static ExPos index(final int colIndex) {
        return new ExPos(0, colIndex);
    }

    public int rowIndex() {
        return this.rowIndex;
    }

    public int colIndex() {
        return this.colIndex;
    }

    public CellRangeAddress region(final int rows, final int cols) {
        /*
         * Build new region based join current position
         */
        final int rowStart = this.rowIndex;
        final int colStart = this.colIndex;
        /*
         * Adjust for end
         */
        int rowEnd = this.rowIndex + rows;
        int colEnd = this.colIndex + cols;
        if (rowStart < rowEnd) {
            rowEnd--;
        }
        if (colStart < colEnd) {
            colEnd--;
        }
        /*
         * valid for region / Merged region A302 must contain 2 or more cells
         *
         */
        final int rowAcc = (rowEnd - rowStart);
        final int colAcc = (colEnd - colStart);
        if (0 < rowAcc || 0 < colAcc) {
            log.debug("[ ZERO ] ( Excel ) 范围扫描: ( Row: {} ~ {}, Column: {} ~ {} )",
                rowStart, rowEnd, colStart, colEnd);
            return new CellRangeAddress(rowStart, rowEnd, colStart, colEnd);
        } else {
            return null;
        }
    }
}
