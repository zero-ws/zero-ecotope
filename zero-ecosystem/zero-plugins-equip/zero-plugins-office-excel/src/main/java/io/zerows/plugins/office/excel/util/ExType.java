package io.zerows.plugins.office.excel.util;

import io.zerows.support.Ut;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author lang : 2024-06-13
 */
class ExType {

    static Object toBoolean(final Cell cell) {
        if (CellType.BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue();
        } else {
            final String literal = cell.getStringCellValue();
            if (Ut.isNil(literal)) {
                return Boolean.FALSE;
            } else {
                return Boolean.valueOf(literal);
            }
        }
    }

    static Object toString(final Cell cell) {
        if (CellType.NUMERIC == cell.getCellType()) {
            /*
             * Fix issue of user operation:
             * Cannot get a STRING value from a NUMERIC cell
             */
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    static Object toNumeric(final Cell cell) {
        // 非数值，直接返回 null
        if (CellType.NUMERIC != cell.getCellType()) {
            return null;
        }


        // 非日期，直接返回数值
        if (!DateUtil.isCellDateFormatted(cell)) {
            return cell.getNumericCellValue();
        }


        final double cellValue = cell.getNumericCellValue();
        // 非合法日期，直接返回 null
        if (!DateUtil.isValidExcelDate(cellValue)) {
            return null;
        }


        final Date date = DateUtil.getJavaDate(cellValue, TimeZone.getDefault());
        /*
         * For 1899-12-30
         */
        final LocalDateTime dateTime = Ut.toDateTime(date);
        if (dateTime.getYear() < 1900) {
            /*
             * Calculation has been put in `toTime`
             */
            final LocalTime time = Ut.toTime(date);
            return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        } else {
            return date.toInstant();
        }
    }
}
