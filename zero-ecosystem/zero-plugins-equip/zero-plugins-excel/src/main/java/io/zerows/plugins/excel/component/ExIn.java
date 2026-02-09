package io.zerows.plugins.excel.component;

import io.zerows.plugins.excel.metadata.ExTable;
import io.zerows.specification.modeling.metadata.HMetaAtom;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 * Two method for
 * 1) Complex Tpl
 * 2) Simple Tpl
 */
public interface ExIn {
    ExIn bind(FormulaEvaluator evaluator);

    ExBound applyTable(ExTable table, Row row, Cell cell, Integer limitation);

    ExTable applyData(ExTable table, ExBound dataRange, Cell cell, HMetaAtom metaAtom);

    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
