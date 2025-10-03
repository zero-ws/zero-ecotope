package io.zerows.epoch.database.jooq.condition;

import io.vertx.core.json.JsonArray;
import io.zerows.component.log.OLog;
import io.zerows.support.Ut;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("all")
public class ClauseString implements Clause {
    @Override
    public Condition where(final Field columnName, final String fieldName, final String op, final Object value) {
        final Term term = termInternal(Pool.TERM_OBJECT_MAP, op);
        return term.where(columnName, fieldName, value);
    }

    protected OLog logger() {
        return Ut.Log.database(getClass());
    }

    /**
     * 日期时间格式的特殊处理模式
     * <pre><code>
     *     扩展操作符
     *     - day:  将输入内容转换成天进行处理
     * </code></pre>
     *
     * @param op
     *
     * @return
     */
    protected Term termDate(final String op) {
        if (Pool.TERM_DATE_IN.containsKey(op)) {
            return termInternal(Pool.TERM_DATE_IN, op);
        }
        return termInternal(Pool.TERM_DATE_MAP, op);
    }

    protected Object normalized(final Object value, final Function<Object, Object> convert) {
        if (value instanceof JsonArray) {
            final JsonArray result = new JsonArray();
            ((JsonArray) value).stream().map(convert)
                .filter(Objects::nonNull).forEach(result::add);
            return result;
        } else {
            return convert.apply(value);
        }
    }

    private Term termInternal(final ConcurrentMap<String, Term> map, final String op) {
        final Term term = map.get(op);
        if (Objects.isNull(term)) {
            logger().warn(Info.JOOQ_TERM_ERR, op);
        } else {
            logger().debug(Info.JOOQ_TERM, term, op);
        }
        return term;
    }
}
