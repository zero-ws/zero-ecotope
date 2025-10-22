package io.zerows.epoch.store.jooq;

import io.r2mo.typed.common.Kv;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-10-22
 */
@Data
@Accessors(fluent = true, chain = true)
public class Join {
    private Class<?> from;
    private String fromOf;
    private Class<?> to;
    private String toOf;

    public Join(final Class<?> from, final String fromOf, final Class<?> to, final String toOf) {
        this.from = from;
        this.fromOf = fromOf;
        this.to = to;
        this.toOf = toOf;
    }

    @Setter(AccessLevel.NONE)
    private List<Kv<Class<?>, String>> others = new ArrayList<>();

    public static Join of(final Class<?> from, final Class<?> to) {
        return new Join(from, null, to, null);
    }

    public static Join of(final Class<?> from, final String fromOf, final Class<?> to) {
        return new Join(from, fromOf, to, null);
    }

    public static Join of(final Class<?> from, final Class<?> to, final String fromOf) {
        return new Join(from, null, to, fromOf);
    }

    public static Join of(final Class<?> from, final String fromOf, final Class<?> to, final String toOf) {
        return new Join(from, fromOf, to, toOf);
    }

    public Join add(final Class<?> joinEntity, final String joinOf) {
        this.others.add(Kv.create(joinEntity, joinOf));
        return this;
    }
}
