package io.zerows.plugins.office.excel.uca.ranger;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.metadata.KRef;
import io.zerows.support.Ut;
import io.zerows.plugins.office.excel.atom.ExRecord;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.plugins.office.excel.util.ExFn;
import io.zerows.specification.modeling.metadata.HMetaAtom;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ComplexIn extends AbstractExIn {
    public ComplexIn(final Sheet sheet) {
        super(sheet);
    }

    @Override
    public ExBound applyTable(final ExTable table, final Row row, final Cell cell, final Integer limitation) {
        /* Scan Field, Once scanning */
        final KRef hod = new KRef();
        ExFn.onRow(this.sheet, row.getRowNum() + 3, row.getRowNum() + 4, (found, foundRow) -> {
            /* Build Field Col */
            final ExBound bound = new ColBound(cell.getColumnIndex(), found.getLastCellNum());
            /* Parent map for extraction */
            final KRef foundParent = new KRef();
            ExFn.itRowZip(found, foundRow, bound, (first, second) -> {
                /* Parent / Child */
                final String parent = first.getStringCellValue();
                final String child = second.getStringCellValue();
                if (Ut.isNotNil(parent) || Ut.isNotNil(child)) {
                    /*
                     * field calculation
                     */
                    if (Ut.isNil(child) && Ut.isNotNil(parent)) {
                        /*
                         * Single field here
                         */
                        table.add(parent);
                    } else if (Ut.isNotNil(child) && Ut.isNotNil(parent)) {
                        /*
                         * Parent found and set the first child
                         */
                        foundParent.add(parent);
                        table.add(parent, child);
                    } else if (Ut.isNotNil(child) && Ut.isNil(parent)) {
                        /*
                         * Add left all child
                         */
                        table.add(foundParent.get(), child);
                    }
                }
            });
            hod.add(new RowBound(foundRow.getRowNum() + 1, limitation));
        });
        return hod.get();
    }

    @Override
    public ExTable applyData(final ExTable table, final ExBound dataRange, final Cell cell, final HMetaAtom metaAtom) {
        /*
         * Build data column range based on current cell and table
         * 1) table means ExTable for range
         * 2) cell is the first row
         */
        final ExBound bound = new ColBound(cell.getColumnIndex(), cell.getColumnIndex() + table.size());
        /*
         * Get index that to checked be `null` for different record here
         */
        final Set<Integer> diffSet = table.indexDiff();
        /* Data Range */
        ExFn.itSheet(this.sheet, dataRange, diffSet, (rowList) -> {
            /*
             *  Build data part instead of each row here
             *  Each row should be record
             * */
            final ExRecord record = new ExRecord(table);
            final ConcurrentMap<String, JsonArray> complexMap = new ConcurrentHashMap<>();
            final ConcurrentMap<String, JsonObject> rowMap = new ConcurrentHashMap<>();
            // --------------- First row only ---------------
            /*
             * The first line is major record
             */
            final Row row = rowList.get(VValue.IDX);
            /*
             * In first iterator for first row, the system should web `complexMap`
             */
            ExFn.itRow(row, bound, this.consumeCellFn(record, rowMap, table, metaAtom));
            this.prepareForComplex(complexMap, rowMap);

            // ----------------- Other row -------------------
            /*
             * From index = 1 to iterate the left row
             */
            final int size = rowList.size();
            if (1 < size) {
                for (int idx = 1; idx < size; idx++) {
                    final Row dataRow = rowList.get(idx);
                    ExFn.itRow(dataRow, bound, this.consumeCellFn(rowMap, table, metaAtom));
                    this.prepareForComplex(complexMap, rowMap);
                }
            }
            // ------------------ Copy JsonArray --------------
            if (!complexMap.isEmpty()) {
                complexMap.forEach(record::put);
            }
            /* Not Empty to add, check whether record is valid */
            if (!record.isEmpty()) {
                table.add(record);
            }
        });
        return table;
    }


    /*
     * Merge `eachMap` to `dataMap`
     */
    protected void prepareForComplex(final ConcurrentMap<String, JsonArray> complexMap,
                                     final ConcurrentMap<String, JsonObject> rowMap) {
        rowMap.forEach((field, record) -> {
            JsonArray original = complexMap.get(field);
            if (Objects.isNull(original)) {
                original = new JsonArray();
            }
            if (!ExRecord.isEmpty(record)) {
                original.add(record);
            }
            complexMap.put(field, original);
        });
        /* Add only once */
        rowMap.clear();
    }

    protected BiConsumer<Cell, HMetaAtom> consumeCellFn(final ConcurrentMap<String, JsonObject> rowMap,
                                                        final String field) {
        return (dataCell, shape) -> {
            /*
             * Calculated
             */
            final String[] fields = field.split("\\.");
            final String parent = fields[0];
            final String child = fields[1];
            /*
             * Do Processing
             */
            JsonObject original = rowMap.get(parent);
            if (Objects.isNull(original)) {
                original = new JsonObject();
            }
            final Class<?> type = shape.type(parent, child);
            final Object value = this.formulaValue(dataCell, type);
            original.put(child, value);
            rowMap.put(parent, original);
        };
    }

    private BiConsumer<Cell, Integer> consumeCellFn(final ExRecord record, final ConcurrentMap<String, JsonObject> rowMap,
                                                    final ExTable table, final HMetaAtom metaAtom) {
        return (dataCell, cellIndex) -> {
            /* Field / Value / field should not be null */
            final String field = table.field(cellIndex);
            if (Objects.nonNull(field)) {
                /* Different get processing */
                if (field.contains(VString.DOT)) {
                    /*
                     * Do Processing
                     */
                    this.consumeCellFn(rowMap, field).accept(dataCell, metaAtom);
                } else {
                    /* Pure Workflow */
                    final Class<?> type = metaAtom.type(field);
                    final Object value = this.formulaValue(dataCell, type);
                    record.put(field, value);
                }
            } else {
                this.logger().warn("Field (index = {0}) could not be found", cellIndex);
            }
        };
    }

    private BiConsumer<Cell, Integer> consumeCellFn(final ConcurrentMap<String, JsonObject> rowMap,
                                                    final ExTable table, final HMetaAtom metaAtom) {
        return (dataCell, cellIndex) -> {
            /* Field / Value / field should not be null */
            final String field = table.field(cellIndex);
            if (Objects.nonNull(field)) {
                if (field.contains(VString.DOT)) {
                    /*
                     * Data Structure for complex data
                     */
                    this.consumeCellFn(rowMap, field).accept(dataCell, metaAtom);
                }
            } else {
                this.logger().warn("Field (index = {0}) could not be found", cellIndex);
            }
        };
    }
}
