package io.zerows.plugins.excel.component;

import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.plugins.excel.util.ExFn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ExValueFn {
    ConcurrentMap<CellType, Function<Cell, Object>> FN_CELL
        = new ConcurrentHashMap<>() {
        {
            this.put(CellType.STRING, ExFn::toString);
            this.put(CellType.BOOLEAN, ExFn::toBoolean);
            this.put(CellType.NUMERIC, ExFn::toNumeric);
        }
    };

    ConcurrentMap<String, Supplier<ExValue>> FN_MATCH = new ConcurrentHashMap<>() {
        {
            this.put(ExcelConstant.CELL.UUID, ExValueUuid::new);
            // 单元格专用解析器（新版）
            this.put(ExcelConstant.CELL.CODE_CLASS, ExValueExpr::new);
            this.put(ExcelConstant.CELL.CODE_CONFIG, ExValueExpr::new);
            this.put(ExcelConstant.CELL.CODE_NAME_CONFIG, ExValueExpr::new);
            this.put(ExcelConstant.CELL.CODE_NAME_CLASS, ExValueExpr::new);
            this.put(ExcelConstant.CELL.NAME_CONFIG, ExValueExpr::new);
            this.put(ExcelConstant.CELL.NAME_CLASS, ExValueExpr::new);
            this.put(ExcelConstant.CELL.NAME_ABBR_CONFIG, ExValueExpr::new);
            // 当前目录专用解析器
            this.put(ExcelConstant.CELL.PWD, ExValuePwd::new);
        }
    };

    ConcurrentMap<String, Supplier<ExValue>> FN_PREFIX = new ConcurrentHashMap<>() {
        {
            this.put(ExcelConstant.CELL.P_JSON, ExValueJson::new);
            this.put(ExcelConstant.CELL.P_FILE, ExValueDynamicFile::new);
            this.put(ExcelConstant.CELL.P_PAGE, ExValueDynamicPage::new);
        }
    };
}
