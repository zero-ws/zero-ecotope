package io.zerows.plugins.office.excel.uca.ranger;

import io.zerows.plugins.office.excel.eon.ExConstant;
import io.zerows.plugins.office.excel.uca.cell.*;
import io.zerows.plugins.office.excel.util.ExFn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2024-06-13
 */
interface Meta {
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
            this.put(ExConstant.CELL.UUID, UuidValue::new);
            // 单元格专用解析器（新版）
            this.put(ExConstant.CELL.CODE_CLASS, ExprValue::new);
            this.put(ExConstant.CELL.CODE_CONFIG, ExprValue::new);
            this.put(ExConstant.CELL.CODE_NAME_CONFIG, ExprValue::new);
            this.put(ExConstant.CELL.NAME_CONFIG, ExprValue::new);
            this.put(ExConstant.CELL.NAME_CLASS, ExprValue::new);
            this.put(ExConstant.CELL.NAME_ABBR_CONFIG, ExprValue::new);
            // 当前目录专用解析器
            this.put(ExConstant.CELL.PWD, PwdValue::new);
        }
    };

    ConcurrentMap<String, Supplier<ExValue>> FN_PREFIX = new ConcurrentHashMap<>() {
        {
            this.put(ExConstant.CELL.P_JSON, PrefixJsonValue::new);
            this.put(ExConstant.CELL.P_FILE, DynamicFileValue::new);
            this.put(ExConstant.CELL.P_PAGE, DynamicPageValue::new);
        }
    };
}
