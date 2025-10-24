package io.zerows.epoch.store.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.typed.common.Kv;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.MMAdapt;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author lang : 2025-10-22
 */
@Deprecated
@Data
@Accessors(fluent = true, chain = true)
public class OldJoin {
    private Class<?> from;
    private String fromField;
    private Class<?> to;
    private String toField;

    public OldJoin(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        this.from = from;
        this.fromField = fromField;
        this.to = to;
        this.toField = toField;
    }

    private OldJoin() {
    }

    @Setter(AccessLevel.NONE)
    private List<Kv<Class<?>, String>> others = new ArrayList<>();

    public OldJoin from(final Class<?> from, final String fromField) {
        this.from = from;
        this.fromField = StrUtil.isEmpty(fromField) ? KName.KEY : fromField;
        return this;
    }

    DBNode forFrom(final Kv<String, String> vectorPojo) {
        return DBNode.of(this.from,
            Optional.ofNullable(vectorPojo.key())
                .map(pojoFile -> MMAdapt.of(pojoFile).vector())
                .orElse(null));
    }

    DBNode forTo(final Kv<String, String> vectorPojo) {
        return DBNode.of(this.to,
            Optional.ofNullable(vectorPojo.value())
                .map(pojoFile -> MMAdapt.of(pojoFile).vector())
                .orElse(null));
    }

    public OldJoin from(final Class<?> from) {
        this.from = from;
        this.fromField = KName.KEY;
        return this;
    }

    public OldJoin to(final Class<?> to, final String toField) {
        this.to = to;
        this.toField = StrUtil.isEmpty(toField) ? KName.KEY : toField;
        return this;
    }

    public OldJoin to(final Class<?> to) {
        this.to = to;
        this.toField = KName.KEY;
        return this;
    }

    public static OldJoin of() {
        return new OldJoin();
    }

    public static OldJoin of(final Class<?> from, final Class<?> to) {
        return new OldJoin(from, KName.KEY, to, KName.KEY);
    }

    public static OldJoin of(final Class<?> from, final String fromField, final Class<?> to) {
        return new OldJoin(from, fromField, to, KName.KEY);
    }

    public static OldJoin of(final Class<?> from, final Class<?> to, final String fromField) {
        return new OldJoin(from, KName.KEY, to, fromField);
    }

    public static OldJoin of(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        return new OldJoin(from, fromField, to, toField);
    }

    public OldJoin add(final Class<?> joinEntity) {
        this.others.add(Kv.create(joinEntity, KName.KEY));
        return this;
    }

    public OldJoin add(final Class<?> joinEntity, final String joinOf) {
        this.others.add(Kv.create(joinEntity, joinOf));
        return this;
    }
}
