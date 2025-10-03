package io.zerows.extension.mbse.basement.uca.query;

import io.zerows.component.qr.syntax.QBranch;
import io.zerows.component.qr.syntax.QLeaf;
import io.zerows.component.qr.syntax.QNode;
import io.zerows.component.qr.syntax.QOp;
import io.zerows.component.qr.syntax.QTree;
import io.zerows.component.qr.syntax.QValue;
import io.zerows.platform.constant.VValue;
import io.zerows.epoch.database.jooq.condition.Clause;
import io.zerows.extension.mbse.basement.atom.element.DataMatrix;
import io.zerows.extension.mbse.basement.uca.jooq.internal.Jq;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


/*
 * 查询分析树解析器
 */
@SuppressWarnings("all")
class QVisitor {

    /*
     * 直接解析，不带表前缀
     */
    static Condition analyze(final QTree tree, final DataMatrix matrix) {
        final QNode node = tree.getData();
        final Set<DataMatrix> set = new HashSet<>();
        set.add(matrix);
        return analyze(node, set, null);
    }

    static Condition analyze(final QTree tree, final Set<DataMatrix> matrix,
                             final ConcurrentMap<String, String> fieldMap) {
        final QNode node = tree.getData();
        return analyze(node, matrix, fieldMap);
    }

    private static Condition analyze(final QNode node, final Set<DataMatrix> matrix,
                                     final ConcurrentMap<String, String> fieldMap) {
        /*
         * 看这个 node 是 哪种
         * 1. Branch：非子节点
         * 2. Value：子节点
         **/
        if (node instanceof QValue) {
            final QLeaf value = (QLeaf) node;
            return analyze(value, matrix, fieldMap);
        } else {
            final List<Condition> conditions = new ArrayList<>();
            final QBranch branch = (QBranch) node;
            branch.nodes().stream().map(each -> analyze(each, matrix, fieldMap))
                .filter(Objects::nonNull)
                .forEach(conditions::add);
            /* 拼条件 */
            return analyze(conditions, node.op());
        }
    }

    private static Condition analyze(final QLeaf leaf, final Set<DataMatrix> matrix,
                                     final ConcurrentMap<String, String> fieldMap) {
        final String field = leaf.field();
        final Field column = Jq.toField(field, matrix, fieldMap);
        if (Objects.isNull(column)) {
            /*
             * Fix: java.lang.NullPointerException
                    at QVisitor.analyze(QVisitor.java:63)
             */
            return null;
        } else {
            final Clause clause = Clause.get(column.getType());
            return clause.where(column, column.getName(),
                /* 特殊 op 处理 */
                leaf.op().value(), leaf.value());
        }
    }

    private static Condition analyze(final List<Condition> conditions, final QOp op) {
        /*
         * 防止数组越界
         */
        if (conditions.isEmpty()) {
            /*
             * Where ignore null;
             */
            return null;
        } else if (1 == conditions.size()) {
            return conditions.get(VValue.IDX);
        } else {
            return (QOp.AND == op) ? DSL.and(conditions) : DSL.or(conditions);
        }
    }
}
