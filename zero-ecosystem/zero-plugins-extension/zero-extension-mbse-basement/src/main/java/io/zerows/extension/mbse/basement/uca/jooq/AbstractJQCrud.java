package io.zerows.extension.mbse.basement.uca.jooq;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.zerows.constant.VValue;
import io.zerows.component.log.Annal;
import io.zerows.extension.mbse.basement.atom.data.DataEvent;
import io.zerows.extension.mbse.basement.atom.element.DataMatrix;
import io.zerows.extension.mbse.basement.atom.element.DataRow;
import io.zerows.extension.mbse.basement.exception._80518Exception500DataTransaction;
import io.zerows.extension.mbse.basement.exception._80519Exception500DataUnexpect;
import io.zerows.extension.mbse.basement.exception._80522Exception417ConditionEmpty;
import io.zerows.extension.mbse.basement.uca.jooq.internal.Jq;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class AbstractJQCrud {
    protected final transient DSLContext context;

    AbstractJQCrud(final DSLContext context) {
        this.context = context;
    }

    protected <R> DataEvent write(final DataEvent event, final BiFunction<String, DataMatrix, R> actorFn, final Predicate<R> testFn) {
        /* 读取所有的数据行（单表也按照多表处理） */
        return this.context.transactionResult(configuration -> this.run(event, (rows) -> rows.forEach(row -> row.matrixData().forEach((table, matrix) -> {
            // 输入检查
            this.ensure(table, matrix);
            // 执行结果（单表）
            final R expected = actorFn.apply(table, matrix);
            // 执行结果（检查）
            this.output(expected, testFn,
                /* 成功 */ () -> row.success(table),
                /* 失败 */ () -> new _80519Exception500DataUnexpect(table, String.valueOf(expected)));
        }))));
    }

    protected DataEvent read(final DataEvent event, final BiFunction<String, DataMatrix, Record> actorFn) {
        return this.context.transactionResult(configuration -> this.run(event, (rows) -> rows.forEach(row -> row.matrixData().forEach((table, matrix) -> {
            // 输入检查
            this.ensure(table, matrix);
            // 执行结果
            final Record record = actorFn.apply(table, matrix);
            // 反向同步记录
            row.success(table, record, new HashSet<>());
        }))));
    }

    protected DataEvent readBatch(final DataEvent event, final BiFunction<String, List<DataMatrix>, Record[]> actorFn) {
        return this.context.transactionResult(configuration -> this.run(event, (rows) -> Jq.argBatch(rows).forEach((table, values) -> {
            /* 执行单表记录 */
            this.ensure(table, values);
            /* 执行结果 */
            final Record[] records = actorFn.apply(table, values);
            // 合并结果集
            this.output(table, rows, records);
        })));
    }

    protected <R> DataEvent writeBatch(final DataEvent event, final BiFunction<String, List<DataMatrix>, R[]> actorFn, final Predicate<R[]> testFn) {
        return this.context.transactionResult(configuration -> this.run(event, (rows) -> Jq.argBatch(rows).forEach((table, values) -> {
            /* 执行单表记录 */
            this.ensure(table, values);
            /* 执行结果（单表）*/
            final R[] expected = actorFn.apply(table, values);
            /* 单张表检查结果 */
            this.output(expected, testFn,
                /* 成功 */ () -> rows.forEach(row -> row.success(table)),
                /* 失败 */ () -> new _80519Exception500DataUnexpect(table, expected.toString())
            );
        })));
    }


    //  ---------------- Private ------------------

    private DataEvent run(final DataEvent event,
                          final Consumer<List<DataRow>> consumer) {
        try {
            final List<DataRow> rows = event.dataRows();
            if (null == rows || rows.isEmpty()) {
                /* 读取不了DataRow，第一层处理 */
                this.logger().error("[ Ox ] 行引用为空，DataRow = null。");
            } else {
                consumer.accept(rows);
            }
        } catch (final DataAccessException ex) {
            this.logger().fatal(ex);
            event.failure(new _80518Exception500DataTransaction(ex));
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
        // Result Here
        if (!event.succeed()) {
            final WebException error = event.getError();
            if (null != error) {
                throw error;
            } else {
                this.logger().error("[ Ox ] 异常为空，但响应也非法。success = {0}", event.succeed());
            }
        }
        return event;
    }

    private void ensure(final String table, final DataMatrix matrix) {
        Fn.jvmKo(matrix.getAttributes().isEmpty(), _80522Exception417ConditionEmpty.class, table);
    }

    private void ensure(final String table, final List<DataMatrix> matrixes) {
        matrixes.forEach(matrix -> this.ensure(table, matrix));
    }

    private <T> void output(final T expected,
                            final Predicate<T> predicate,
                            final Actuator actuator,
                            final Supplier<WebException> supplier/* 使用函数为延迟调用 */) {
        if (Objects.isNull(predicate)) {            /* 不关心执行结果影响多少行 */
            Fn.jvmAt(actuator);
        } else {
            if (predicate.test(expected)) {         /* 关心结果，执行条件检查 */
                Fn.jvmAt(actuator);
            } else {
                throw supplier.get();               /* 检查不通过抛出异常 */
            }
        }
    }

    private void output(final String table, final List<DataRow> rows, final Record[] records) {
        for (int idx = VValue.IDX; idx < rows.size(); idx++) {              /* 两个数据集按索引合并 */
            final DataRow row = rows.get(idx);
            if (null != row) {
                if (idx < records.length) {
                    final Record record = records[idx];
                    row.success(table, record, new HashSet<>());            /* 直接调用内置方法 */
                } else {
                    row.success(table, null, new HashSet<>());       /* 空数据返回 */
                }
            }
        }
    }

    private Annal logger() {
        return Annal.get(this.getClass());
    }
}
