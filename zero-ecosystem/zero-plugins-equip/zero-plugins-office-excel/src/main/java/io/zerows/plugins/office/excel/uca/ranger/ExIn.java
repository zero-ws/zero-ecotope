package io.zerows.plugins.office.excel.uca.ranger;

import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.specification.modeling.metadata.HMetaAtom;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

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

    default OLog logger() {
        return Ut.Log.plugin(this.getClass());
    }
}
