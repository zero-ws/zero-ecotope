package io.zerows.extension.mbse.basement.uca.query;

import io.zerows.enums.modeling.EmModel;
import io.zerows.component.qr.Criteria;
import io.zerows.component.qr.Sorter;
import io.zerows.extension.mbse.basement.atom.element.DataTpl;
import io.zerows.extension.mbse.basement.exception._80503Exception501IngestImplement;
import io.zerows.extension.mbse.basement.uca.metadata.AoSentence;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/*
 * 专用查询条件转换接口
 * 会被 doQuery 方法调用
 */
public interface Ingest {

    static Ingest create(final EmModel.Type type) {
        return Pool.INGEST_POOL.get(type);
    }

    default Ingest bind(final AoSentence sentence) {
        /* 什么都不做，但可绑定 */
        return this;
    }

    /*
     * 1. 查询条件处理
     */
    default Condition onCondition(final DataTpl tpl,
                                  final Criteria criteria) {
        throw new _80503Exception501IngestImplement();
    }

    /*
     * 2. 排序条件解析
     */
    @SuppressWarnings("all")
    default List<OrderField> onOrder(final DataTpl tpl,
                                     final Sorter sorter) {
        throw new _80503Exception501IngestImplement();
    }

    /*
     * 3. 表专用处理
     */
    default Table<Record> onTable(final DataTpl tpl,
                                  final Set<String> tables) {
        throw new _80503Exception501IngestImplement();
    }

    /*
     * 下边方法是 join 模式专用，对于 DIRECT 模式不提供实现
     */
    default Condition onCondition(final DataTpl tpl,
                                  final Criteria criteria,
                                  final ConcurrentMap<String, String> aliasMap) {
        throw new _80503Exception501IngestImplement();
    }

    @SuppressWarnings("all")
    default List<OrderField> onOrder(final DataTpl tpl,
                                     final Sorter sorter,
                                     final ConcurrentMap<String, String> aliasMap) {
        throw new _80503Exception501IngestImplement();
    }

    default Table<Record> onTable(final DataTpl tpl,
                                  final Set<String> tables,
                                  final ConcurrentMap<String, String> aliasMap) {
        throw new _80503Exception501IngestImplement();
    }
}
