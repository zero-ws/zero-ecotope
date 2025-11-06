package io.zerows.extension.module.mbsecore.component.jooq.internal;

import io.r2mo.function.Fn;
import io.zerows.extension.module.mbsecore.exception._80523Exception417ConditionWhere;
import io.zerows.extension.module.mbsecore.metadata.element.DataMatrix;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class IWhere {

    static Condition key(final DataMatrix matrix) {
        final Set<String> keys = matrix.getKeys();
        Fn.jvmKo(keys.isEmpty(), _80523Exception417ConditionWhere.class);
        final Set<Condition> conditions = keys.stream()
            .map(field -> IWhere.Cond.eq(field, matrix))
            .collect(Collectors.toSet());
        return DSL.and(conditions);
    }

    static Condition keys(final List<DataMatrix> matrixList) {
        final Iterator<DataMatrix> it = matrixList.iterator();
        Fn.jvmKo(!it.hasNext(), _80523Exception417ConditionWhere.class);
        Condition condition = key(it.next());
        while (it.hasNext()) {
            condition = condition.or(key(it.next()));
        }
        return condition;
    }

    /* = 操作 */
    public interface Cond {
        @SuppressWarnings("all")
        public static Condition eq(final String field,
                                   final DataMatrix matrix) {
            final Field column = Meta.field(field, matrix);
            final Object value = matrix.getValue(field);
            return column.eq(value);
        }
    }
}
