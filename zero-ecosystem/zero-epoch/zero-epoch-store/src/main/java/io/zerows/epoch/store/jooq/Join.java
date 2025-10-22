package io.zerows.epoch.store.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.common.Kv;
import io.zerows.epoch.constant.KName;
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
    private String fromField;
    private Class<?> to;
    private String toField;

    public Join(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        this.from = from;
        this.fromField = fromField;
        this.to = to;
        this.toField = toField;
    }

    private Join() {
    }

    @Setter(AccessLevel.NONE)
    private List<Kv<Class<?>, String>> others = new ArrayList<>();

    public Join from(final Class<?> from, final String fromField) {
        this.from = from;
        this.fromField = StrUtil.isEmpty(fromField) ? KName.KEY : fromField;
        return this;
    }

    public Join from(final Class<?> from) {
        this.from = from;
        this.fromField = KName.KEY;
        return this;
    }

    public Join to(final Class<?> to, final String toField) {
        this.to = to;
        this.toField = StrUtil.isEmpty(toField) ? KName.KEY : toField;
        return this;
    }

    public Join to(final Class<?> to) {
        this.to = to;
        this.toField = KName.KEY;
        return this;
    }

    public static Join of() {
        return new Join();
    }

    public static Join of(final Class<?> from, final Class<?> to) {
        return new Join(from, KName.KEY, to, KName.KEY);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to) {
        return new Join(from, fromField, to, KName.KEY);
    }

    public static Join of(final Class<?> from, final Class<?> to, final String fromField) {
        return new Join(from, KName.KEY, to, fromField);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        return new Join(from, fromField, to, toField);
    }

    public Join add(final Class<?> joinEntity) {
        this.others.add(Kv.create(joinEntity, KName.KEY));
        return this;
    }

    public Join add(final Class<?> joinEntity, final String joinOf) {
        this.others.add(Kv.create(joinEntity, joinOf));
        return this;
    }
}
