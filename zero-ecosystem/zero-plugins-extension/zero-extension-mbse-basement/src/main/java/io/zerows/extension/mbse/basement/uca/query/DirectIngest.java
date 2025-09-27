package io.zerows.extension.mbse.basement.uca.query;

import io.zerows.core.uca.log.Annal;
import io.zerows.core.uca.qr.Criteria;
import io.zerows.core.uca.qr.Sorter;
import io.zerows.core.uca.qr.syntax.QTree;
import io.zerows.extension.mbse.basement.uca.jooq.internal.Jq;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.mbse.basement.atom.element.DataMatrix;
import io.zerows.extension.mbse.basement.atom.element.DataTpl;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

class DirectIngest implements Ingest {

    private static final Annal LOGGER = Annal.get(DirectIngest.class);

    @Override
    public Condition onCondition(final DataTpl tpl,
                                 final Criteria criteria) {
        /* 构造查询树 */
        final QTree tree = QTree.create(criteria);
        LOG.SQL.info(tree.hasValue(), LOGGER, "（Direct模式）查询分析树：\n{0}", tree.toString());
        final DataMatrix matrix = this.getMatrix(tpl);
        return QVisitor.analyze(tree, matrix);
    }

    @Override
    @SuppressWarnings("all")
    public List<OrderField> onOrder(final DataTpl tpl, final Sorter sorter) {
        final List<OrderField> orders = new ArrayList<>();
        final JsonObject data = sorter.toJson();
        for (final String field : data.fieldNames()) {
            final String columnName = tpl.column(field);
            if (Objects.nonNull(columnName)) {
                final boolean isAsc = data.getBoolean(field);
                final Field column = DSL.field(columnName);
                orders.add(isAsc ? column.asc() : column.desc());
            }
        }
        LOG.SQL.info(0 < orders.size(), LOGGER, "（Direct模式）排序条件：{0}, size = {1}", data.encode(), orders.size());
        return orders;
    }

    @Override
    public Table<Record> onTable(final DataTpl tpl, final Set<String> tables) {
        final String table = tables.iterator().next();
        return Jq.toTable(table);
    }

    private DataMatrix getMatrix(final DataTpl tpl) {
        /* 抽取Tpl中的查询条件，DIRECT模式仅考虑单表 */
        final ConcurrentMap<String, DataMatrix> matrixs
            = tpl.matrixData();
        /* 解析查询分析树 */
        return matrixs.values().iterator().next();
    }
}
