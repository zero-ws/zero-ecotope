package io.zerows.plugins.office.excel.uca.ranger;

import io.zerows.support.Ut;
import io.zerows.plugins.office.excel.atom.ExKey;
import io.zerows.plugins.office.excel.util.ExOut;
import org.apache.poi.ss.usermodel.*;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractExIn implements ExIn {
    protected transient Sheet sheet;
    protected transient FormulaEvaluator evaluator;

    public AbstractExIn(final Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public ExIn bind(final FormulaEvaluator evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    /**
     * 值准备阶段，值准备阶段主要会从 {@link Cell} 出发做值解析，更多是兼容 Excel 或表格本身的值格式来实现值的提取流程
     *
     * @param dataCell {@link Cell} 实例
     * @param type     类型
     *
     * @return 返回解析后的值
     */
    @SuppressWarnings("all")
    protected Object formulaValue(final Cell dataCell, final Class<?> type) {
        Object result;
        try {
            /*
             * 如果单元格解析失败，直接跳出，返回 null 值
             * BLANK / ERROR
             */
            final CellType cellType = dataCell.getCellType();
            if (CellType.BLANK == cellType || CellType.ERROR == cellType) {
                return null;
            }

            if (CellType.FORMULA == cellType && Objects.nonNull(this.evaluator)) {
                return this.formulaExpr(dataCell);
            }

            /*
             * 重新计算函数，基于如下两种情况进行计算：
             * 1. Cell 值 -> 解析表格
             * 2. Shape 值 -> 元数据处理
             */
            final Function<Cell, Object> fun = this.formulaFn(dataCell, type);
            if (Objects.isNull(fun)) {
                return null;
            }

            result = fun.apply(dataCell);
        } catch (final Throwable ex) {
            this.logger().fatal(ex);
            ex.printStackTrace();
            result = null;
        }
        return result;
    }

    protected Function<Cell, Object> formulaFn(final Cell dataCell, final Class<?> type) {
        // 如果类型是 FORMULA，则直接使用 STRING 的函数来处理
        final Function<Cell, Object> fun;
        final CellType cellType = dataCell.getCellType();
        if (CellType.FORMULA == cellType) {
            fun = Meta.FN_CELL.get(CellType.STRING);
        } else {
            if (Objects.nonNull(type)) {
                final CellType switchedType = ExOut.type(type);
                if (Objects.isNull(switchedType)) {
                    fun = Meta.FN_CELL.get(cellType);
                } else {
                    fun = Meta.FN_CELL.get(switchedType);
                }
            } else {
                fun = Meta.FN_CELL.get(cellType);
            }
        }
        return fun;
    }

    protected String formulaExpr(final Cell dataCell) {
        // 先处理 Formula 类型的值
        final CellValue cellValue = this.evaluator.evaluate(dataCell);
        final String exprValue = cellValue.getStringValue();


        // 字符串类型的空值，直接返回 null
        if (Ut.isNil(exprValue)) {
            return null;
        }


        // 非字符串类型空值，查看内置的值是否是 NULL / EMPTY 两个值，此两个语义值也直接返回 null
        if (ExKey.VALUE_NULL.equalsIgnoreCase(exprValue.trim())) {
            return null;
        }


        // 上述条件都满足的情况下，返回解析值
        return exprValue;
    }
}
